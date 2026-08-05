package com.example.nexcashapplication.feature.transaction.presentation.detail

sealed interface DetailTransactionEvent {
    data object OnBackClick : DetailTransactionEvent
    data object OnEditClick : DetailTransactionEvent
    data object OnDeleteClick : DetailTransactionEvent
    data object OnRefresh : DetailTransactionEvent
}
