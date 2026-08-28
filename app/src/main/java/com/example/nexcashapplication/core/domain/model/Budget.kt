package com.example.nexcashapplication.core.domain.model

import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import java.time.LocalDate

data class Budget(
    val id: String? = null,
    val categoryId: String,
    val startDate: LocalDate,       // epoch millis, tanggal mulai berlaku
    val endDate: LocalDate?,        // null = berlaku terus (recurring tanpa batas)
    val amount: Long,
    val recurrenceType: RecurrenceType,
)
