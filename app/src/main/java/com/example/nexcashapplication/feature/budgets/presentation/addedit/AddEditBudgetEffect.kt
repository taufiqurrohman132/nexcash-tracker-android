package com.example.nexcashapplication.feature.budgets.presentation.addedit


sealed interface AddEditBudgetEffect {
    data object NavigateBack : AddEditBudgetEffect
    data class ShowSnackbar(val message: String) : AddEditBudgetEffect
    data object ShowDiscardConfirmation : AddEditBudgetEffect
}