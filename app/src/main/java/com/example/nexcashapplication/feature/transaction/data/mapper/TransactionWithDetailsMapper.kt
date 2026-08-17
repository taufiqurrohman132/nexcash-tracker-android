package com.example.nexcashapplication.feature.transaction.data.mapper

import com.example.nexcashapplication.core.datasource.local.model.TransactionWithDetailsEntity
import com.example.nexcashapplication.feature.aset.data.mapper.toDomain
import com.example.nexcashapplication.feature.category.data.mapper.toDomain
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails

fun TransactionWithDetailsEntity.toDomain() = TransactionWithDetails(
    transaction = transaction.toDomain(),
    category = category?.toDomain(),
    account = account?.toDomain(),
)