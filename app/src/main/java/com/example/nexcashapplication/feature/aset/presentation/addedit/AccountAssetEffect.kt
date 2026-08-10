package com.example.nexcashapplication.feature.aset.presentation.addedit

sealed interface AccountAssetEffect {
    data object NavigateToAddAccount : AccountAssetEffect
    data class NavigateToAccountDetail(val accountId: String) : AccountAssetEffect
    data object NavigateToAllBankAccounts : AccountAssetEffect
    data class NavigateToInvestmentDetail(val investmentId: String) : AccountAssetEffect
    data object NavigateToAddAsset : AccountAssetEffect
    data class ShowError(val message: String) : AccountAssetEffect
}