package com.example.nexcashapplication.feature.transaction.domain.model

data class Transaction(
    val id: String,
    val amount: Long,
    val categoryId: String?,
    val note: String,
    val timestamp: Long,
    val isIncome: Boolean,
    val accountId: String
)
