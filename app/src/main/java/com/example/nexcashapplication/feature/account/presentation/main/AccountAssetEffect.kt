package com.example.nexcashapplication.feature.account.presentation.main

import com.example.nexcashapplication.core.i18n.UiText

sealed interface AccountAssetEffect {
    data object NavigateToAddAccount : AccountAssetEffect
    data class NavigateToAccountDetail(val accountId: String?) : AccountAssetEffect
    data class ShowError(val message: UiText) : AccountAssetEffect
}
