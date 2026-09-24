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
    recurringGroupId = recurringGroupId,
)

fun Budget.toEntity(): BudgetEntity {
    // Baris baru selalu dapat id segar; baris existing memakai id-nya sendiri.
    val rowId = id ?: UUID.randomUUID().toString()
    return BudgetEntity(
        id = rowId,
        categoryId = categoryId,
        startDate = startDate.toEpochMillis(),
        endDate = endDate.toEpochMillis(),
        amount = amount,
        periodType = recurrenceType.name.lowercase(),
        // Root seri WAJIB self-reference (bukan NULL): unique index (recurring_group_id, start_date)
        // tidak menjamin apa-apa untuk NULL di SQLite, sehingga budget ganda bisa lolos.
        // Anak seri sudah membawa recurringGroupId = id root dari hasil generate.
        recurringGroupId = recurringGroupId ?: rowId,
        isRecurring = isRecurring,
    )
}
