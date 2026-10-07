package com.example.nexcashapplication.feature.account.presentation.detail

import com.example.nexcashapplication.core.i18n.UiText

sealed interface DetailAccountEffect {
    data object NavigateBack : DetailAccountEffect
    data class NavigateToAssetSettings(val assetId: String) : DetailAccountEffect
    data class NavigateToAddTransaction(val assetId: String) : DetailAccountEffect
    data class NavigateToAllTransactions(val assetId: String) : DetailAccountEffect
    data class NavigateToTransactionDetail(val transactionId: String) : DetailAccountEffect
    data class NavigateToEditAssetName(val assetId: String) : DetailAccountEffect
    data object NavigateBackAfterDelete : DetailAccountEffect
    data class ShowSnackbar(val message: UiText) : DetailAccountEffect
    data class ShowToast(val message: UiText) : DetailAccountEffect
}
