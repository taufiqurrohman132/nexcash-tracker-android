package com.example.nexcashapplication.feature.notification.presentation.edit

import com.example.nexcashapplication.core.i18n.UiText
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class NotificationSettingsStateTest {

    @Test
    fun `default values NotificationSettingsState sesuai harapan`() {
        val state = NotificationSettingsState()

        assertTrue(state.masterEnabled)
        assertTrue(state.remindersActive)
        assertTrue(state.permissionGranted)
        assertTrue(state.budgets.isEmpty())
        assertTrue(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `BudgetAlertUi - data class properties dan copy`() {
        val alertUi = BudgetAlertUi(
            budgetId = "b1",
            name = "Transportasi",
            iconKey = "car",
            enabled = true,
            thresholds = setOf(80, 100),
        )

        assertEquals("b1", alertUi.budgetId)
        assertEquals("Transportasi", alertUi.name)
        assertEquals("car", alertUi.iconKey)
        assertTrue(alertUi.enabled)
        assertEquals(setOf(80, 100), alertUi.thresholds)

        val updated = alertUi.copy(enabled = false, thresholds = setOf(100))
        assertFalse(updated.enabled)
        assertEquals(setOf(100), updated.thresholds)
    }
}
