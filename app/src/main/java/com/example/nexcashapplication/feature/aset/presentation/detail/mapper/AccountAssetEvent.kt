package com.example.nexcashapplication.feature.aset.presentation.detail.mapper

sealed interface AccountAssetEvent {
    data object OnToggleBalanceVisibility : AccountAssetEvent
    data object OnAddAccountClick : AccountAssetEvent
    data object OnSeeAllBankAccountsClick : AccountAssetEvent
    data class OnAccountClick(val accountId: String) : AccountAssetEvent
    data class OnInvestmentClick(val investmentId: String) : AccountAssetEvent
    data object OnAddAssetClick : AccountAssetEvent
    data object OnRetry : AccountAssetEvent
}