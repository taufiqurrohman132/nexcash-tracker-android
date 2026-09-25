package com.example.nexcashapplication.feature.category.domain.usecase

import com.example.nexcashapplication.core.common.toEpochMillis
import com.example.nexcashapplication.core.datasource.local.result.CategoryExpenseResult
import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.repository.BudgetRepository
import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.budgets.domain.usecase.GetBudgetPeriodUseCase
import com.example.nexcashapplication.feature.common.utils.toEndOfDayMillis
import com.example.nexcashapplication.feature.common.utils.toStartOfDayMillis
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate
import javax.inject.Inject

/** Anggaran satu sub kategori: budget sendiri (0 bila tidak punya) + jumlah terpakai. */
data class SubCategoryBudgetDetail(
    val category: Category,
    val totalBudget: Long,
    val usedBudget: Long,
)

/**
 * Hasil [GetSubCategoriesWithBudgetUseCase] untuk satu induk:
 * - [parentBudgetAmount] / [parentUsedAmount]: anggaran induk. Terpakai induk bersifat
 *   **rollup** (transaksi induk + seluruh sub) — tapi 0 bila induk belum punya budget,
 *   sesuai perilaku kartu anggaran yang hanya berguna saat budget ada.
 * - [subCategories]: tiap sub lengkap dengan budget & terpakainya.
 */
data class SubCategoriesBudgetResult(
    val parentBudgetAmount: Long,
    val parentUsedAmount: Long,
    val subCategories: List<SubCategoryBudgetDetail>,
)

/**
 * Budget + jumlah terpakai untuk semua sub kategori dari satu induk — pola **hybrid**:
 *
 * - sub **tanpa** budget sendiri → terpakai dihitung pada *periode induk*
 *   (bulan berjalan bila induk belum punya budget), sehingga "n% dari budget induk"
 *   valid karena periodenya sama dengan angka induk.
 * - sub **dengan** budget sendiri → terpakai dihitung pada periode budgetnya sendiri,
 *   supaya `terpakai / budget` pada kartunya konsisten.
 * - rollup induk memakai periode induk dan selalu menghitung semua sub,
 *   terlepas dari apakah sub punya budget sendiri (budget sub = limit terpisah,
 *   bukan pengurang budget induk — jadi tidak ada double-count).
 */
class GetSubCategoriesWithBudgetUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
    private val getBudgetPeriodUseCase: GetBudgetPeriodUseCase,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(parentId: String): Flow<SubCategoriesBudgetResult> =
        categoryRepository.getSubCategories(parentId).flatMapLatest { subCategories ->
            if (subCategories.isEmpty()) {
                flowOf(
                    SubCategoriesBudgetResult(
                        parentBudgetAmount = 0L,
                        parentUsedAmount = 0L,
                        subCategories = emptyList(),
                    ),
                )
            } else {
                val today = LocalDate.now()
                combine(
                    budgetRepository.getActiveBudgetsForCategories(
                        categoryIds = subCategories.map { it.id },
                        referenceDate = today.toEpochMillis(),
                    ),
                    budgetRepository.getActiveBudgetForCategory(parentId, today),
                ) { subBudgets, parentBudget -> subBudgets to parentBudget }
                    .flatMapLatest { (subBudgets, parentBudget) ->
                        observeUsedAmounts(
                            parentId = parentId,
                            subCategories = subCategories,
                            subBudgets = subBudgets.associateBy { it.categoryId },
                            parentBudget = parentBudget,
                        )
                    }
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeUsedAmounts(
        parentId: String,
        subCategories: List<Category>,
        subBudgets: Map<String, Budget>,
        parentBudget: Budget?,
    ): Flow<SubCategoriesBudgetResult> {
        val (parentStart, parentEnd) = parentBudget?.let { periodMillis(it) }
            ?: currentMonthMillis(LocalDate.now())

        // Sub ber-budget sendiri dikelompokkan per periode budgetnya — periode berbeda
        // berarti window berbeda, jadi query terpisah per kelompok.
        val ownPeriodGroups: List<Pair<Pair<Long, Long>, List<String>>> =
            subCategories
                .mapNotNull { sub -> subBudgets[sub.id]?.let { budget -> periodMillis(budget) to sub.id } }
                .groupBy({ it.first }, { it.second })
                .map { (period, ids) -> period to ids }

        // Posisi hasil tiap sub ber-budget di dalam array [combine] (0 = periode induk).
        val ownIndexById: Map<String, Int> =
            ownPeriodGroups
                .flatMapIndexed { groupIndex, (_, ids) ->
                    ids.map { id -> id to groupIndex + 1 }
                }
                .toMap()

        // Satu query keluarga (induk + semua sub, periode induk) cukup untuk rollup induk
        // sekaligus terpakai sub tanpa budget sendiri.
        val flows: List<Flow<List<CategoryExpenseResult>>> =
            buildList {
                add(
                    transactionRepository.getUsedAmountsForCategories(
                        categoryIds = listOf(parentId) + subCategories.map { it.id },
                        periodStart = parentStart,
                        periodEnd = parentEnd,
                    ),
                )
                ownPeriodGroups.forEach { (period, ids) ->
                    add(
                        transactionRepository.getUsedAmountsForCategories(
                            categoryIds = ids,
                            periodStart = period.first,
                            periodEnd = period.second,
                        ),
                    )
                }
            }

        return combine(flows) { results ->
            val familyUsed = results.first().associateBy { it.categoryId }

            val details = subCategories.map { sub ->
                val budget = subBudgets[sub.id]
                val usedBudget = if (budget != null) {
                    results[ownIndexById.getValue(sub.id)]
                        .firstOrNull { it.categoryId == sub.id }
                        ?.total ?: 0L
                } else {
                    familyUsed[sub.id]?.total ?: 0L
                }
                SubCategoryBudgetDetail(
                    category = sub,
                    totalBudget = budget?.amount ?: 0L,
                    usedBudget = usedBudget,
                )
            }

            SubCategoriesBudgetResult(
                parentBudgetAmount = parentBudget?.amount ?: 0L,
                // Rollup: seluruh baris keluarga (induk + semua sub, periode induk).
                // Tanpa budget induk kartu anggaran tidak menampilkan terpakai (0),
                // tapi sub tetap punya angka terpakainya sendiri di atas.
                parentUsedAmount = if (parentBudget == null) {
                    0L
                } else {
                    results.first().sumOf { it.total }
                },
                subCategories = details,
            )
        }
    }

    /** Window millis (awal hari .. akhir hari) dari periode sebuah budget. */
    private fun periodMillis(budget: Budget): Pair<Long, Long> {
        val (start, end) = getBudgetPeriodUseCase(
            isRecurring = budget.isRecurring,
            type = budget.recurrenceType,
            startDate = budget.startDate,
            endDate = budget.endDate,
        )
        return start.toStartOfDayMillis() to end.toEndOfDayMillis()
    }

    /** Fallback periode induk tanpa budget: bulan berjalan (1 .. akhir bulan). */
    private fun currentMonthMillis(today: LocalDate): Pair<Long, Long> =
        today.withDayOfMonth(1).toStartOfDayMillis() to
            today.withDayOfMonth(today.lengthOfMonth()).toEndOfDayMillis()
}
