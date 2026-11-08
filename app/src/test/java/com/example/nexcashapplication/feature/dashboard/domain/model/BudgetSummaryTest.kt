package com.example.nexcashapplication.feature.dashboard.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BudgetSummaryTest {

    @Test
    fun `remainingAmount - menghitung selisih limit dan spent dengan benar`() {
        val summary = BudgetSummary(limitAmount = 1_000_000L, spentAmount = 350_000L)
        assertEquals(650_000L, summary.remainingAmount)
    }

    @Test
    fun `remainingAmount - bernilai nol ketika spent sama dengan limit`() {
        val summary = BudgetSummary(limitAmount = 500_000L, spentAmount = 500_000L)
        assertEquals(0L, summary.remainingAmount)
    }

    @Test
    fun `remainingAmount - bernilai negatif ketika spent melebihi limit (defisit)`() {
        val summary = BudgetSummary(limitAmount = 500_000L, spentAmount = 750_000L)
        assertEquals(-250_000L, summary.remainingAmount)
    }

    @Test
    fun `percentage - menghitung persentase pemakaian budget dengan benar`() {
        val summary = BudgetSummary(limitAmount = 1_000_000L, spentAmount = 500_000L)
        assertEquals(50.0f, summary.percentage, 0.001f)
    }

    @Test
    fun `percentage - bernilai nol jika limitAmount adalah 0 (mencegah division by zero)`() {
        val summary = BudgetSummary(limitAmount = 0L, spentAmount = 100_000L)
        assertEquals(0.0f, summary.percentage)
    }

    @Test
    fun `percentage - bernilai nol jika spentAmount adalah 0`() {
        val summary = BudgetSummary(limitAmount = 1_000_000L, spentAmount = 0L)
        assertEquals(0.0f, summary.percentage)
    }

    @Test
    fun `percentage - dapat bernilai lebih dari 100 persen saat over budget`() {
        val summary = BudgetSummary(limitAmount = 1_000_000L, spentAmount = 1_500_000L)
        assertEquals(150.0f, summary.percentage, 0.001f)
    }

    @Test
    fun `data class equality and copy`() {
        val original = BudgetSummary(limitAmount = 100_000L, spentAmount = 40_000L)
        val copy = original.copy(spentAmount = 50_000L)

        assertEquals(100_000L, copy.limitAmount)
        assertEquals(50_000L, copy.spentAmount)
        assertEquals(50_000L, copy.remainingAmount)
        assertTrue(original != copy)
    }
}
