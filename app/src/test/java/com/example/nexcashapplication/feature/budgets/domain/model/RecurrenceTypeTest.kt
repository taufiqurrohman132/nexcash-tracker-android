package com.example.nexcashapplication.feature.budgets.domain.model

import com.example.nexcashapplication.R
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RecurrenceTypeTest {

    @Test
    fun `labelResId - setiap RecurrenceType memiliki string resource valid`() {
        assertEquals(R.string.budget_recurrence_custom, RecurrenceType.CUSTOM.labelResId)
        assertEquals(R.string.budget_recurrence_weekly, RecurrenceType.WEEKLY.labelResId)
        assertEquals(R.string.budget_recurrence_monthly, RecurrenceType.MONTHLY.labelResId)

        for (type in RecurrenceType.entries) {
            assertTrue(type.labelResId != 0, "labelResId untuk ${type.name} tidak boleh 0")
        }
    }
}
