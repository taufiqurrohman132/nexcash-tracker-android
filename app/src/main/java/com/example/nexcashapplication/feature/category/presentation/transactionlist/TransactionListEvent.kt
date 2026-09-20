package com.example.nexcashapplication.feature.category.presentation.transactionlist

// TransactionListEvent.kt
sealed interface TransactionListEvent {
    data object OnBackClick : TransactionListEvent
    data class OnTransactionClick(val transactionId: String) : TransactionListEvent
    data object OnRetryClick : TransactionListEvent
}
