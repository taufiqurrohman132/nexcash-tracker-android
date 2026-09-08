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

}