package com.example.nexcashapplication.feature.transaction.data.mapper

import com.example.nexcashapplication.core.datasource.local.model.TransactionEntity
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import kotlin.time.Instant

fun TransactionEntity.toDomain() = Transaction(
    id = id,
    amount = amount,
    title = title,
    categoryId = categoryId,
    description = description,
    dateTime = Instant.fromEpochMilliseconds(transactionDate),
    isIncome = type == "income",
    accountId = accountId,
)

fun Transaction.toEntity() = TransactionEntity(
    id = id,
    amount = amount,
    title = title,
    categoryId = categoryId,
    description = description,
    transactionDate = dateTime.toEpochMilliseconds(),
    type = if (isIncome) "income" else "expense",
    accountId = accountId,
    source = "",
)