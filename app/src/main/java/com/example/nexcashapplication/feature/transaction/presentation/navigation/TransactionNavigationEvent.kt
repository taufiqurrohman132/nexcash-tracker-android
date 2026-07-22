package com.example.nexcashapplication.feature.transaction.presentation.navigation

sealed interface TransactionNavigationEvent {
    data object NavigateToAddTransaction : TransactionNavigationEvent
    data class NavigateToDetail(val transactionId: String) : TransactionNavigationEvent
}