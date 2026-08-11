package com.example.nexcashapplication.feature.aset.presentation.addedit.typeoption

sealed interface AddEditAccountEffect {
    data object NavigateToAddAddEditAccount : AddEditAccountEffect
    data class NavigateToAddEditAccountDetail(val accountId: String) : AddEditAccountEffect
    data object NavigateToAllBankAccounts : AddEditAccountEffect
    data class NavigateToInvestmentDetail(val investmentId: String) : AddEditAccountEffect
    data object NavigateToAddAddEdit : AddEditAccountEffect
    data class ShowError(val message: String) : AddEditAccountEffect
}