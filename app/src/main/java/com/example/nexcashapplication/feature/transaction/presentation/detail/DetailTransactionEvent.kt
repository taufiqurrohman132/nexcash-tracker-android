package com.example.nexcashapplication.feature.transaction.presentation.detail

sealed interface DetailTransactionEvent {
    data object OnBackClick : DetailTransactionEvent
    data object OnEditClick : DetailTransactionEvent
    data object OnDuplicateClick : DetailTransactionEvent
    data object OnShareClick : DetailTransactionEvent
    data object OnDeleteClick : DetailTransactionEvent
    data object OnDeleteConfirm : DetailTransactionEvent
    data object OnDismissDeleteDialog : DetailTransactionEvent
    data object OnAttachmentClick : DetailTransactionEvent
}
