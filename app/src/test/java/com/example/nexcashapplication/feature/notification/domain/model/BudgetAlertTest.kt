package com.example.nexcashapplication.feature.notification.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BudgetAlertTest {

    @Test
    fun `membuat BudgetAlert dengan field yang benar`() {
        val alert = BudgetAlert(
            id = "budget-1-80",
            budgetId = "budget-1",
            categoryName = "Belanja",
            percentUsed = 85,
            threshold = 80,
            periodLabel = "November",
            periodStart = 1700000000000L,
        )

        assertEquals("budget-1-80", alert.id)
        assertEquals("budget-1", alert.budgetId)
        assertEquals("Belanja", alert.categoryName)
        assertEquals(85, alert.percentUsed)
        assertEquals(80, alert.threshold)
        assertEquals("November", alert.periodLabel)
        assertEquals(1700000000000L, alert.periodStart)
    }

    @Test
    fun `BudgetAlertPreference - nilai default sesuai spesifikasi`() {
        val pref = BudgetAlertPreference(budgetId = "b1")

        assertEquals("b1", pref.budgetId)
        assertTrue(pref.enabled)
        assertEquals(setOf(80, 100), pref.thresholds)
    }

    @Test
    fun `BudgetAlertPreference - copy memodifikasi thresholds dan status enabled`() {
        val pref = BudgetAlertPreference(budgetId = "b1")
        val updated = pref.copy(enabled = false, thresholds = setOf(100))

        assertEquals("b1", updated.budgetId)
        assertTrue(!updated.enabled)
        assertEquals(setOf(100), updated.thresholds)
    }
}
