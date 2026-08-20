package com.example.nexcashapplication.feature.transaction.presentation.main

sealed interface TransactionEffect {
    data object NavigateToAddTransaction : TransactionEffect
    data class NavigateToDetail(val transactionId: String) : TransactionEffect
    data class ShowSnackbar(val message: String) : TransactionEffect
}