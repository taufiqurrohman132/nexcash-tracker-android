package com.example.nexcashapplication.feature.budgets.presentation.editmultiple

import java.time.YearMonth

sealed interface EditPresentageBudgetEvent {
    data class OnPeriodClick(val initialMonthYear: YearMonth) : EditPresentageBudgetEvent
    data class OnPeriodSelected(val selectedMonthYear: YearMonth) : EditPresentageBudgetEvent
    data class OnSliderChange(val id: String, val newPercent: Int) : EditPresentageBudgetEvent
    data class OnAmountInputChange(val id: String, val newAmount: Long) : EditPresentageBudgetEvent
    data object OnBackClick : EditPresentageBudgetEvent
    data object OnSaveClick : EditPresentageBudgetEvent
    data object OnAddCategoryClick : EditPresentageBudgetEvent

    // Hapus alokasi kategori (tutup seri) — ikon hapus per kartu kategori
    data class OnDeleteBudgetClick(val id: String) : EditPresentageBudgetEvent
    data object OnConfirmDeleteClick : EditPresentageBudgetEvent
    data object OnDismissDeleteClick : EditPresentageBudgetEvent
}