package com.example.nexcashapplication.feature.aset.presentation.detail

sealed interface DetailAccountEffect {
    data object NavigateBack : DetailAccountEffect
    data class NavigateToAssetSettings(val assetId: Long) : DetailAccountEffect
    data class NavigateToAddTransaction(val assetId: Long) : DetailAccountEffect
    data class NavigateToTransfer(val assetId: Long) : DetailAccountEffect
    data class NavigateToAllTransactions(val assetId: Long) : DetailAccountEffect
    data class NavigateToTransactionDetail(val transactionId: Long) : DetailAccountEffect
    data class NavigateToEditAssetName(val assetId: Long) : DetailAccountEffect
    data object NavigateBackAfterDelete : DetailAccountEffect
    data class ShowSnackbar(val message: String) : DetailAccountEffect
}