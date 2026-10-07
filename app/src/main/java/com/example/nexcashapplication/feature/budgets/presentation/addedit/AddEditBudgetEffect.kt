package com.example.nexcashapplication.feature.budgets.presentation.addedit

import com.example.nexcashapplication.core.i18n.UiText


sealed interface AddEditBudgetEffect {
    data object NavigateBack : AddEditBudgetEffect
    data class ShowSnackbar(val message: UiText) : AddEditBudgetEffect
    data object ShowDiscardConfirmation : AddEditBudgetEffect
}
