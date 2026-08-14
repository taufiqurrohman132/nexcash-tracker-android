package com.example.nexcashapplication.feature.aset.data.mapper

import com.example.nexcashapplication.core.datasource.local.model.AccountEntity
import com.example.nexcashapplication.feature.aset.domain.model.AccountModel
import com.example.nexcashapplication.feature.aset.domain.model.TypeAccount
import java.util.UUID

fun AccountModel.toEntity() = AccountEntity(
    id = id.ifBlank { UUID.randomUUID().toString() },
    name = name,
    accountType = accountType.name.lowercase(),
    provider = providerAccount,
    currentBalance = currentBalance,
    isActive = isActive,
    deletedAt = deletedAt,
    lastUpdateAt = System.currentTimeMillis(),
)

fun AccountEntity.toDomain() = AccountModel(
    id = id,
    name = name,
    accountType = TypeAccount.valueOf(accountType.uppercase()),
    providerAccount = provider,
    currentBalance = currentBalance,
    isActive = isActive,
    deletedAt = deletedAt,
    lastUpdateAt = lastUpdateAt,
)