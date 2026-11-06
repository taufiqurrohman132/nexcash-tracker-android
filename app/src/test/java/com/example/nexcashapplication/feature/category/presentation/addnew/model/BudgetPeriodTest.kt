package com.example.nexcashapplication.feature.category.presentation.addnew.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BudgetPeriodTest {

    @Test
    fun `memverifikasi label dari setiap BudgetPeriod enum`() {
        assertEquals("Bulanan", BudgetPeriod.BULANAN.label)
        assertEquals("Mingguan", BudgetPeriod.MINGGUAN.label)
        assertEquals("Sekali Pakai", BudgetPeriod.SEKALI_PAKAI.label)
    }
}
