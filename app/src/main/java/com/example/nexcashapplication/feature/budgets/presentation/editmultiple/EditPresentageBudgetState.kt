package com.example.nexcashapplication.feature.budgets.presentation.editmultiple

import com.example.nexcashapplication.feature.budgets.presentation.editmultiple.model.EditPresentageBudgetItemUiModel
import java.time.YearMonth

data class EditPresentageBudgetState(
    val budgetId: String? = null,

    /** Periode (bulan) yang sedang diedit alokasinya. */
    val period: YearMonth = YearMonth.now(),

    /** Total pot anggaran bulan ini (baseline saat load; denominator persentase). */
    val totalBudget: Long = 0L,
    val totalUsed: Long = 0L,
    val totalRemaining: Long = 0L,
    val budgets: List<EditPresentageBudgetItemUiModel> = emptyList(),
    val showMonthPicker: Boolean = false,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
) {
    /**
     * Total persentase alokasi (tiap item dibulatkan ke bawah).
     * Karena persentase = amount / totalBudget, total ≤ 100% selama
     * jumlah amount ≤ totalBudget (pot tidak berubah saat edit).
     */
    val totalPercentage: Int
        get() = budgets.sumOf { it.percentage.toInt() }

    /** Valid bila: ada alokasi, total ≤ 100%, dan tiap kategori dialokasikan > 0. */
    val isValid: Boolean
        get() = budgets.isNotEmpty() &&
                totalPercentage <= 100 &&
                budgets.all { it.totalBudget > 0L }
}
