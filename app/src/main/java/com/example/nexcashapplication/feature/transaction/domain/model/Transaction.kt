package com.example.nexcashapplication.feature.transaction.domain.model

import kotlinx.datetime.Instant


data class Transaction(
    val id: String,
    val title: String,
    val amount: Long,
    val categoryId: String?,
    val description: String?,
    val dateTime: Instant,
    val isIncome: Boolean,
    val accountId: String?,
)
