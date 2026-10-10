package com.example.nexcashapplication.feature.budgets.presentation.editmultiple

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.common.toEndOfMonthMillis
import com.example.nexcashapplication.core.common.toEpochMillis
import com.example.nexcashapplication.core.common.toStartOfMonthMillis
import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.repository.BudgetRepository
import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.core.i18n.uiText
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import com.example.nexcashapplication.feature.budgets.domain.usecase.DeleteBudgetUseCase
import com.example.nexcashapplication.feature.budgets.domain.usecase.SaveBudgetUseCase
import com.example.nexcashapplication.feature.budgets.presentation.editmultiple.model.EditPresentageBudgetItemUiModel
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.core.domain.model.TypeCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

/**
 * Layar edit alokasi banyak kategori sekaligus.
 *
 * Model data:
 * - `totalBudget` (pot) = jumlah alokasi saat load → jadi denominator persentase
 *   dan TIDAK berubah selama user mengedit (redistribusi, bukan tambah total).
 * - `budgets[i].totalBudget` = nominal alokasi kategori tersebut;
 *   `percentage` selalu diturunkan dari nominal / pot.
 *
 * Simpan: tiap item dialirkan ke [SaveBudgetUseCase] (MONTHLY, anchor pada
 * periode terpilih) — sehingga mengikuti aturan series+instance yang sama
 * (instance aktif + periode mendatang ikut berubah, riwayat aman).
 */
@HiltViewModel
class EditPresentageBudgetViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
    private val saveBudgetUseCase: SaveBudgetUseCase,
    private val deleteBudgetUseCase: DeleteBudgetUseCase,
) : BaseViewModel<EditPresentageBudgetState, EditPresentageBudgetEvent, EditPresentageBudgetEffect>(
    EditPresentageBudgetState(),
) {

    /** Periode sumber reload; diganti saat user memilih bulan lain. */
    private val periodFlow = MutableStateFlow(YearMonth.now())

    init {
        observeData()
    }

    override fun onEvent(event: EditPresentageBudgetEvent) {
        when (event) {
            EditPresentageBudgetEvent.OnBackClick -> {
                viewModelScope.launch {
                    sendEffect(EditPresentageBudgetEffect.NavigateBack)
                }
            }

            EditPresentageBudgetEvent.OnAddCategoryClick -> {
                viewModelScope.launch {
                    sendEffect(EditPresentageBudgetEffect.NavigateToAddCategory)
                }
            }

            EditPresentageBudgetEvent.OnSaveClick -> handleSave()

            is EditPresentageBudgetEvent.OnDeleteBudgetClick ->
                // Dialog konfirmasi per kartu kategori
                updateState { it.copy(deleteConfirmationId = event.id) }

            EditPresentageBudgetEvent.OnDismissDeleteClick ->
                updateState { it.copy(deleteConfirmationId = null) }

            EditPresentageBudgetEvent.OnConfirmDeleteClick -> handleDelete()

            // Persentase → nominal: amount = pot * persen / 100 (pot tetap selama edit)
            is EditPresentageBudgetEvent.OnSliderChange -> {
                val pot = state.value.totalBudget
                val newAmount = pot * event.newPercent / 100L
                updateState { st ->
                    st.copy(
                        budgets = st.budgets.map {
                            if (it.id == event.id) {
                                it.copy(
                                    percentage = event.newPercent.toFloat(),
                                    totalBudget = newAmount,
                                )
                            } else it
                        },
                    )
                }
            }

            // Nominal → persentase: percentage = amount / pot * 100
            is EditPresentageBudgetEvent.OnAmountInputChange -> {
                val pot = state.value.totalBudget
                val newPercentage =
                    if (pot > 0L) event.newAmount * 100f / pot else 0f
                updateState { st ->
                    st.copy(
                        budgets = st.budgets.map {
                            if (it.id == event.id) {
                                it.copy(
                                    totalBudget = event.newAmount,
                                    percentage = newPercentage,
                                )
                            } else it
                        },
                    )
                }
            }

            is EditPresentageBudgetEvent.OnPeriodClick ->
                updateState { it.copy(showMonthPicker = true) }

            is EditPresentageBudgetEvent.OnPeriodSelected -> {
                val month = event.selectedMonthYear
                if (month != periodFlow.value) {
                    // Reload data untuk bulan baru (Flow Room → state ikut berubah)
                    periodFlow.value = month
                    updateState { it.copy(showMonthPicker = false, isLoading = true) }
                } else {
                    updateState { it.copy(showMonthPicker = false) }
                }
            }
        }
    }

    /**
     * Simpan seluruh alokasi periode terpilih.
     *
     * Validasi → loop per kategori ke [SaveBudgetUseCase] → Snackbar error pertama
     * bila ada yang gagal, atau [EditPresentageBudgetEffect.NavigateBack] bila semua sukses.
     */
    private fun handleSave() {
        val st = state.value
        when {
            st.budgets.isEmpty() -> {
                sendEffectDirect(
                    EditPresentageBudgetEffect.ShowSnackbar(
                        uiText(R.string.budget_error_min_one_category),
                    ),
                )
                return
            }

            !st.isValid -> {
                sendEffectDirect(
                    EditPresentageBudgetEffect.ShowSnackbar(
                        uiText(R.string.budget_error_allocation_total, st.totalPercentage),
                    ),
                )
                return
            }
        }

        updateState { it.copy(isSaving = true) }
        viewModelScope.launch {
            // Anchor di dalam periode terpilih supaya window MONTHLY jatuh di bulan itu
            // dan lookup instance aktif menemukan budget yang sedang berjalan.
            val anchor = st.period.atDay(
                minOf(LocalDate.now().dayOfMonth, st.period.lengthOfMonth()),
            )

            var firstError: UiText? = null
            for (item in st.budgets) {
                if (item.totalBudget <= 0L) continue
                val result = saveBudgetUseCase(
                    SaveBudgetUseCase.Request(
                        categoryId = item.id, // id item = id kategori
                        amount = item.totalBudget,
                        periodType = RecurrenceType.MONTHLY,
                        customStart = anchor, // tidak dipakai untuk MONTHLY
                        customEnd = anchor,
                        isRecurringRequested = true,
                        today = anchor,
                    ),
                )
                if (result is SaveBudgetUseCase.Result.Failure && firstError == null) {
                    firstError = result.message
                }
            }

            updateState { it.copy(isSaving = false) }
            if (firstError == null) {
                sendEffect(EditPresentageBudgetEffect.NavigateBack)
            } else {
                sendEffect(EditPresentageBudgetEffect.ShowSnackbar(firstError))
            }
        }
    }

    private fun sendEffectDirect(effect: EditPresentageBudgetEffect) {
        viewModelScope.launch { sendEffect(effect) }
    }

    /**
     * Konfirmasi hapus alokasi satu kategori: cari instance aktifnya di periode
     * terpilih, lalu tutup serinya via [DeleteBudgetUseCase] (periode berjalan +
     * mendatang ikut terhapus, riwayat lampau tetap tersimpan).
     * Daftar kartu ikut berubah otomatis karena load-nya berbasis Flow Room.
     */
    private fun handleDelete() {
        val categoryId = state.value.deleteConfirmationId ?: return
        val anchor = anchorFor(state.value.period)

        viewModelScope.launch {
            val active = budgetRepository
                    .getActiveBudgetForCategory(categoryId, anchor)
                    .first()

            val result = if (active == null) {
                DeleteBudgetUseCase.Result.Failure(
                    uiText(R.string.budget_error_budget_not_found),
                )
            } else {
                deleteBudgetUseCase(active, today = anchor)
            }

            updateState { it.copy(deleteConfirmationId = null) }
            when (result) {
                is DeleteBudgetUseCase.Result.Success ->
                    sendEffect(
                        EditPresentageBudgetEffect.ShowSnackbar(
                            uiText(R.string.budget_allocation_deleted),
                        ),
                    )

                is DeleteBudgetUseCase.Result.Failure ->
                    sendEffect(EditPresentageBudgetEffect.ShowSnackbar(result.message))
            }
        }
    }

    // ── Load ──────────────────────────────────────────────────────────────

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeData() {
        periodFlow
                .flatMapLatest { period ->
                    val anchor = anchorFor(period)
                    categoryRepository.getExpenseCategories()
                            .flatMapLatest { categories ->
                                val ids = categories.map { it.id }
                                if (ids.isEmpty()) {
                                    flowOf(PeriodAllocation(period, emptyList(), 0L))
                                } else {
                                    combine(
                                        budgetRepository.getActiveBudgetsForCategories(
                                            categoryIds = ids,
                                            referenceDate = anchor.toEpochMillis(),
                                        ),
                                        transactionRepository.getTransactionsForCategories(ids),
                                    ) { budgets, transactions ->
                                        buildAllocation(period, categories, budgets, transactions)
                                    }
                                }
                            }
                }
                .onEach { allocation ->
                    updateState {
                        it.copy(
                            period = allocation.period,
                            totalBudget = allocation.totalBudget,
                            totalUsed = allocation.totalUsed,
                            totalRemaining = allocation.totalBudget - allocation.totalUsed,
                            budgets = allocation.items,
                            isLoading = false,
                        )
                    }
                }
                .launchIn(viewModelScope)
    }

    private data class PeriodAllocation(
        val period: YearMonth,
        val items: List<EditPresentageBudgetItemUiModel>,
        val totalUsed: Long,
    ) {
        val totalBudget: Long get() = items.sumOf { it.totalBudget }
    }

    private fun buildAllocation(
        period: YearMonth,
        categories: List<Category>,
        budgets: List<Budget>,
        transactions: List<Transaction>,
    ): PeriodAllocation {
        // Query aktif urut start_date ASC → associateBy "last menang" = instance terbaru
        val budgetsByCategory = budgets.associateBy { it.categoryId }

        // Hanya kategori yang punya alokasi di periode ini yang tampil
        val items = categories.mapNotNull { category ->
            val budget = budgetsByCategory[category.id] ?: return@mapNotNull null
            if (budget.amount <= 0L) return@mapNotNull null
            EditPresentageBudgetItemUiModel(
                id = category.id,
                name = category.name,
                percentage = 0f, // dihitung setelah total pot diketahui
                color = parseColor(category.color),
                totalBudget = budget.amount,
            )
        }

        val total = items.sumOf { it.totalBudget }
        val withPercentage = items.map {
            it.copy(
                percentage = if (total > 0L) it.totalBudget * 100f / total else 0f,
            )
        }

        // Terpakai = pengeluaran bulan [period] pada kategori yang dialokasikan
        val monthStart = period.toStartOfMonthMillis()
        val monthEnd = period.toEndOfMonthMillis()
        val allocatedIds = withPercentage.map { it.id }.toSet()
        val used = transactions.filter { tx ->
            tx.typeTransaction == TypeCategory.EXPENSE &&
                    tx.categoryId != null && tx.categoryId in allocatedIds &&
                    tx.dateTime.toEpochMilliseconds() in monthStart..monthEnd
        }.sumOf { it.amount }

        return PeriodAllocation(period, withPercentage, used)
    }

    /** Anchor di dalam bulan periode (hari ini bila bulan berjalan). */
    private fun anchorFor(period: YearMonth): LocalDate =
        period.atDay(minOf(LocalDate.now().dayOfMonth, period.lengthOfMonth()))

    private fun parseColor(hex: String): Color = try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        Color.Gray
    }
}
