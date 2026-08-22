package com.example.nexcashapplication.feature.transaction.presentation.detail.state

data class DeleteTransactionDialogUiState(
    val title: String,
    val formattedAmount: String,
    val typeLabel: String,
    val accountName: String,
    val balanceEffectText: String, // udah di-resolve, bukan dihitung di Composable
)