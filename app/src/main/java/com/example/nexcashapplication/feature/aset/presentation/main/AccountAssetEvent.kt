package com.example.nexcashapplication.feature.aset.presentation.main

import com.example.nexcashapplication.feature.aset.domain.model.TypeAccount

sealed interface AccountAssetEvent {
    data object OnToggleBalanceVisibility : AccountAssetEvent
    data object OnAddAccountClick : AccountAssetEvent
    data class OnSeeAllAccountsClick(val type: TypeAccount) : AccountAssetEvent
    data class OnAccountClick(val accountId: String) : AccountAssetEvent
    data object OnRetry : AccountAssetEvent
}