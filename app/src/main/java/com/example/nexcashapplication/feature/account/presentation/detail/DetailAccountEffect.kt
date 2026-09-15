package com.example.nexcashapplication.feature.account.presentation.detail

sealed interface DetailAccountEffect {
    data object NavigateBack : DetailAccountEffect
    data class NavigateToAssetSettings(val assetId: String) : DetailAccountEffect
    data class NavigateToAddTransaction(val assetId: String) : DetailAccountEffect
    data class NavigateToAllTransactions(val assetId: String) : DetailAccountEffect
    data class NavigateToTransactionDetail(val transactionId: String) : DetailAccountEffect
    data class NavigateToEditAssetName(val assetId: String) : DetailAccountEffect
    data object NavigateBackAfterDelete : DetailAccountEffect
    data class ShowSnackbar(val message: String) : DetailAccountEffect
    data class ShowToast(val message: String) : DetailAccountEffect
}