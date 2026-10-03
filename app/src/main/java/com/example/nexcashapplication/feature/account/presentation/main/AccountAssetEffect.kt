package com.example.nexcashapplication.feature.account.presentation.main

sealed interface AccountAssetEffect {
    data object NavigateToAddAccount : AccountAssetEffect
    data class NavigateToAccountDetail(val accountId: String?) : AccountAssetEffect
    data class ShowError(val message: String) : AccountAssetEffect
}