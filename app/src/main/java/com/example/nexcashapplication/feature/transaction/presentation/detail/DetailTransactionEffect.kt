package com.example.nexcashapplication.feature.transaction.presentation.detail

sealed interface DetailTransactionEffect {
    data class NavigateToEdit(val transactionId: String) : DetailTransactionEffect
    data object NavigateBack : DetailTransactionEffect
//    data class ShowFullscreenAttachment(val attachment: Attachment) : TransactionDetailEffect
    data class ShareTransaction(val shareText: String) : DetailTransactionEffect
    data class ShowSnackbar(val message: String) : DetailTransactionEffect
    data object ShowDeleteConfirmationDialog : DetailTransactionEffect
}