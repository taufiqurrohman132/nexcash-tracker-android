package com.example.nexcashapplication.core.domain.model

import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import java.time.LocalDate

data class Budget(
    val id: String? = null,
    val categoryId: String,
    val startDate: LocalDate,       // tanggal mulai periode ini berlaku
    val endDate: LocalDate,         // tanggal akhir periode ini
    val amount: Long,
    val recurrenceType: RecurrenceType,
    val isRecurring: Boolean,
    // Root seri = self-reference (id sendiri); anak seri = id root.
    // null hanya saat membuat root baru — mapper mengisinya dengan id hasil generate.
    val recurringGroupId: String? = null,
) {
    /** Root seri: baris yang recurring_group_id menunjuk ke id-nya sendiri. */
    val isSeriesRoot: Boolean
        get() = recurringGroupId != null && recurringGroupId == id
}
