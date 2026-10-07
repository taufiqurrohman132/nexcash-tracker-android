package com.example.nexcashapplication.feature.transaction.presentation.main

import com.example.nexcashapplication.core.i18n.UiText

sealed interface TransactionEffect {
    data object NavigateToAddTransaction : TransactionEffect
    data class NavigateToDetail(val transactionId: String) : TransactionEffect
    data class ShowSnackbar(val message: UiText) : TransactionEffect
}
