package com.example.nexcashapplication.feature.transaction.presentation.detail.mapper

import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import com.example.nexcashapplication.feature.transaction.presentation.detail.DetailTransactionState


fun TransactionWithDetails.toDetailTrasactionState() = DetailTransactionState(
    transactionId = transaction.id,
    name = transaction.title,
    dateTime = transaction.dateTime,
    amount = transaction.amount,
    type = transaction.typeTransaction,
    category = category,
    account = account,
)