package com.example.nexcashapplication.core.datasource.local.mapper

import com.example.nexcashapplication.core.common.toEpochMillis
import com.example.nexcashapplication.core.common.toLocalDate
import com.example.nexcashapplication.core.datasource.local.model.BudgetEntity
import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import java.util.UUID

fun BudgetEntity.toDomain() = Budget(
    id = id,
    categoryId = categoryId,
    startDate = startDate.toLocalDate(),
    endDate = endDate.toLocalDate(),
    amount = amount,
    recurrenceType = RecurrenceType.valueOf(periodType.uppercase()),
    isRecurring = isRecurring,
)

fun Budget.toEntity() = BudgetEntity(
    id = id ?: UUID.randomUUID().toString(),
    categoryId = categoryId,
    startDate = startDate.toEpochMillis(),
    endDate = endDate.toEpochMillis(),
    amount = amount,
    periodType = recurrenceType.name.lowercase(),
    recurringGroupId = null,
    isRecurring = isRecurring,
)

