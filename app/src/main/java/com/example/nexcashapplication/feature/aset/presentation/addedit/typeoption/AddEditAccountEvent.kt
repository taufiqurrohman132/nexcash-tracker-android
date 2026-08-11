package com.example.nexcashapplication.feature.aset.presentation.addedit.typeoption

sealed interface AddEditAccountEvent {
    data object OnToggleBalanceVisibility : AddEditAccountEvent
    data object OnAddAddEditAccountClick : AddEditAccountEvent
    data object OnSeeAllBankAccountsClick : AddEditAccountEvent
    data class OnAddEditAccountClick(val accountId: String) : AddEditAccountEvent
    data class OnInvestmentClick(val investmentId: String) : AddEditAccountEvent
    data object OnAddClickAddEdit : AddEditAccountEvent
    data object OnRetry : AddEditAccountEvent
}