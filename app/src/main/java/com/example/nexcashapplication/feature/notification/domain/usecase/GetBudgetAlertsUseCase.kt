package com.example.nexcashapplication.feature.notification.domain.usecase

import com.example.nexcashapplication.core.common.toEpochMillis
import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.repository.BudgetRepository
import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.budgets.domain.usecase.GetBudgetPeriodUseCase
import com.example.nexcashapplication.feature.common.utils.toEndOfDayMillis
import com.example.nexcashapplication.feature.common.utils.toStartOfDayMillis
import com.example.nexcashapplication.feature.notification.domain.model.BudgetAlert
import com.example.nexcashapplication.feature.notification.domain.model.BudgetAlertPreference
import com.example.nexcashapplication.feature.notification.domain.repository.NotificationPreferenceRepository
import com.example.nexcashapplication.feature.settings.domain.repository.SettingsRepository
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

/**
 * Peringatan anggaran: budget expense aktif yang pemakaiannya melewati threshold
 * 80% / 100% di periode berjalan, satu alert per threshold yang terlewati.
 *
 * Aturan yang dilindungi test/regression di sini:
 * - **Rollup subkategori**: transaksi anak dihitung ke budget induknya (family id =
 *   induk + seluruh anak), supaya persennya selaras dengan layar kategori/budget.
 * - **Window periode** memakai [GetBudgetPeriodUseCase] (WEEKLY/MONTHLY/CUSTOM), bukan
 *   bulan kalender mutlak — budget mingguan tidak boleh dihitung sepanjang bulan.
 * - Hanya `TransactionType.EXPENSE`; persen dibulatkan ke bawah dan mentok di 0..100.
 *
 * Alert muncul/hilang mengikuti pemakaian aktual (sumbernya Room, jadi reaktif).
 * Id `"$budgetId-$threshold"` dibentuk di sini karena stabilitasnya bagian dari aturan.
 */
class GetBudgetAlertsUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
    private val getBudgetPeriodUseCase: GetBudgetPeriodUseCase,
    private val notificationPreferenceRepository: NotificationPreferenceRepository,
    private val settingsRepository: SettingsRepository,
) {
    /**
     * Alert = budget yang melewati threshold **yang masih aktif di pengaturan**.
     * Saklar master mati → tidak ada alert sama sekali (layar daftar kosong).
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<BudgetAlert>> =
        combine(
            computeAlerts(),
            notificationPreferenceRepository.preferences,
            settingsRepository.settings.map { it.notificationMasterEnabled },
        ) { alerts, preferences, masterEnabled ->
            if (!masterEnabled) return@combine emptyList()

            val prefByBudget = preferences.associateBy { it.budgetId }
            alerts.filter { alert ->
                // belum pernah disetel → default: on, threshold 80 & 100
                val pref = prefByBudget[alert.budgetId] ?: BudgetAlertPreference(alert.budgetId)
                pref.enabled && alert.threshold in pref.thresholds
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun computeAlerts(): Flow<List<BudgetAlert>> {
        return categoryRepository.getExpenseCategories().flatMapLatest { categories ->
            val parentIds = categories.map { it.id }
            if (parentIds.isEmpty()) {
                flowOf(emptyList<BudgetAlert>())
            } else {
                categoryRepository.getAllCategory().flatMapLatest { allCategories ->
                    val childIdsByParent = allCategories
                            .filterNotNull()
                            .mapNotNull { child -> child.parentId?.let { it to child.id } }
                            .groupBy({ it.first }, { it.second })

                    combine(
                        budgetRepository.getActiveBudgetsForCategories(
                            categoryIds = parentIds,
                            referenceDate = LocalDate.now().toEpochMillis(),
                        ),
                        transactionRepository.getTransactionsForCategories(
                            (parentIds + parentIds.flatMap { childIdsByParent[it].orEmpty() })
                                    .distinct(),
                        ),
                    ) { budgets, transactions ->
                        buildAlerts(categories, budgets, transactions, childIdsByParent)
                    }
                }
            }
        }
    }

    /** Ubah budget aktif menjadi alert untuk tiap threshold yang terlewati (80% → 100%). */
    private fun buildAlerts(
        categories: List<Category>,
        budgets: List<Budget>,
        transactions: List<Transaction>,
        childIdsByParent: Map<String, List<String>>,
    ): List<BudgetAlert> {
        val nameByCategory = categories.associate { it.id to it.name }

        return budgets.asSequence()
                .filter { it.amount > 0L && it.id != null }
                .mapNotNull { budget ->
                    val name = nameByCategory[budget.categoryId] ?: return@mapNotNull null
                    val percent = usedPercent(budget, transactions, childIdsByParent)
                    if (percent < WARNING_PERCENT) return@mapNotNull null
                    Triple(budget, name, percent)
                }
                .flatMap { (budget, name, percent) ->
                    sequence {
                        if (percent >= LIMIT_PERCENT) {
                            yield(
                                alertOf(budget, name, percent, LIMIT_PERCENT),
                            )
                        }
                        yield(alertOf(budget, name, percent, WARNING_PERCENT))
                    }
                }
                .toList()
    }

    /** Persen terpakai (dibulatkan ke bawah, mentok 0..100) di periode berjalan budget ini. */
    private fun usedPercent(
        budget: Budget,
        transactions: List<Transaction>,
        childIdsByParent: Map<String, List<String>>,
    ): Int {
        if (budget.amount <= 0L) return 0

        val (start, end) = getBudgetPeriodUseCase(
            isRecurring = budget.isRecurring,
            type = budget.recurrenceType,
            startDate = budget.startDate,
            endDate = budget.endDate,
        )
        val periodStart = start.toStartOfDayMillis()
        val periodEnd = end.toEndOfDayMillis()

        val familyIds = setOf(budget.categoryId) + childIdsByParent[budget.categoryId].orEmpty()
        val used = transactions
                .filter { tx ->
                    tx.typeTransaction == TransactionType.EXPENSE &&
                            tx.categoryId != null && tx.categoryId in familyIds &&
                            tx.dateTime.toEpochMilliseconds() in periodStart..periodEnd
                }
                .sumOf { it.amount }

        return ((used * 100) / budget.amount).toInt().coerceIn(0, 100)
    }

    private fun alertOf(
        budget: Budget,
        name: String,
        percentUsed: Int,
        threshold: Int,
    ): BudgetAlert {
        val (start, _) = getBudgetPeriodUseCase(
            isRecurring = budget.isRecurring,
            type = budget.recurrenceType,
            startDate = budget.startDate,
            endDate = budget.endDate,
        )
        return BudgetAlert(
            id = "${budget.id}-$threshold",
            budgetId = budget.id!!,
            categoryName = name,
            percentUsed = percentUsed,
            threshold = threshold,
            periodLabel = start.format(monthFormatter),
        )
    }

    private companion object {
        const val WARNING_PERCENT = 80
        const val LIMIT_PERCENT = 100
        val monthFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("MMMM", Locale.getDefault())
    }
}
