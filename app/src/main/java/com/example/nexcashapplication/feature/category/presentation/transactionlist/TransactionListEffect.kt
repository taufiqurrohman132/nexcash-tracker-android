package com.example.nexcashapplication.feature.category.presentation.transactionlist

// TransactionListEffect.kt
sealed interface TransactionListEffect {
    data object NavigateBack : TransactionListEffect
    data class NavigateToTransactionDetail(val transactionId: String) : TransactionListEffect
}
