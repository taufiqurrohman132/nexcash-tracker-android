package com.example.nexcashapplication.feature.budgets.presentation.editmultiple

import com.example.nexcashapplication.feature.budgets.presentation.editmultiple.model.EditPresentageBudgetItemUiModel

data class EditPresentageBudgetState(
    val budgetId: String? = null,
    val totalBudget: Long = 0L,
    val totalUsed: Long = 0L,
    val totalRemaining: Long = 0L,
    val budgets: List<EditPresentageBudgetItemUiModel> = emptyList(),
    val showMonthPicker: Boolean = false,
)