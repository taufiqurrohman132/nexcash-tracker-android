package com.example.nexcashapplication.feature.transaction.data.mapper

import com.example.nexcashapplication.core.datasource.local.model.TransactionEntity
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType
import java.util.UUID
import kotlin.time.Instant

fun TransactionEntity.toDomain() = Transaction(
    id = id,
    amount = amount,
    title = title,
    categoryId = categoryId,
    description = description,
    dateTime = Instant.fromEpochMilliseconds(transactionDate),
    typeTransaction = TransactionType.valueOf(type.uppercase()),
    accountId = accountId,
)

fun Transaction.toEntity() = TransactionEntity(
    id = id ?: UUID.randomUUID().toString(),
    amount = amount,
    title = title,
    categoryId = categoryId,
    description = description,
    transactionDate = dateTime.toEpochMilliseconds(),
    type = typeTransaction.name.lowercase(),
    accountId = accountId,
    source = "",
)