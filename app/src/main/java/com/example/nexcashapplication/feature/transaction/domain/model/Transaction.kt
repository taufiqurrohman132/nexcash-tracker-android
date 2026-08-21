package com.example.nexcashapplication.feature.transaction.domain.model

import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType
import kotlin.time.Clock
import kotlin.time.Instant


data class Transaction(
    val id: String? = null,
    val title: String,
    val amount: Long,
    val categoryId: String?,
    val description: String?,
    val dateTime: Instant = Clock.System.now(),
    val typeTransaction: TransactionType,
    val accountId: String?,
)
