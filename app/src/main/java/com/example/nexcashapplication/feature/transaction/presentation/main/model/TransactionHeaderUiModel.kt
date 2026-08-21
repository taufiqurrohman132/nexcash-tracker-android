package com.example.nexcashapplication.feature.transaction.presentation.main.model

data class TransactionHeaderUiModel(
    val id: String,
    val title: String,
    val time: String,       // "10:45"
    val totalAmount: Long,
    val isExpanded: Boolean,
)
