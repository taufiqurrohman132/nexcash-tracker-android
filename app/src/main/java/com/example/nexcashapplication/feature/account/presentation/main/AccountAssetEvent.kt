package com.example.nexcashapplication.feature.account.presentation.main

sealed interface AccountAssetEvent {
    data object OnToggleBalanceVisibility : AccountAssetEvent
    data object OnAddAccountClick : AccountAssetEvent
    data class OnAccountClick(val accountId: String?) : AccountAssetEvent
    data object OnRetry : AccountAssetEvent
}