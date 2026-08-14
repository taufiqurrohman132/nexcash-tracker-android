package com.example.nexcashapplication.feature.aset.presentation.main

import com.example.nexcashapplication.feature.aset.domain.model.TypeAccount

sealed interface AccountAssetEffect {
    data object NavigateToAddAccount : AccountAssetEffect
    data class NavigateToAccountDetail(val accountId: String) : AccountAssetEffect
    data class NavigateToAllAccounts(val typeAccount: TypeAccount) : AccountAssetEffect
    data class ShowError(val message: String) : AccountAssetEffect
}