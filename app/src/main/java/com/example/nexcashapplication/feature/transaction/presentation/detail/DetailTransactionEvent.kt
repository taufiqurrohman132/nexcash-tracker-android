package com.example.nexcashapplication.feature.transaction.presentation.detail

sealed interface DetailTransactionEvent {
    data class LoadTransaction(val transactionId: String) : DetailTransactionEvent
    data object EditClicked : DetailTransactionEvent
    data object DuplicateClicked : DetailTransactionEvent
    data object ShareClicked : DetailTransactionEvent
    data object DeleteClicked : DetailTransactionEvent
    data object DeleteConfirmed : DetailTransactionEvent
    data object DeleteCancelled : DetailTransactionEvent
//    data class AttachmentClicked(val attachment: Attachment) : DetailTransactionEvent

}
