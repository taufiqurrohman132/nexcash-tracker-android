package com.example.nexcashapplication.feature.dashboard.presentation.state

import com.example.nexcashapplication.core.i18n.UiText
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DashboardStateTest {

    @Test
    fun `default values - memastikan nilai inisialisasi state awal sesuai`() {
        val state = DashboardState()

        assertEquals("Budi", state.userName)
        assertEquals(14_560_200L, state.totalBalance)
        assertEquals(12_500_000L, state.income)
        assertEquals(3_245_000L, state.expense)
        assertEquals(1_755_000L, state.remainingBudget)
        assertEquals(0.65f, state.budgetUsedPercentage, 0.001f)
        assertTrue(state.transactions.isEmpty())
        assertTrue(state.isLoading)
        assertTrue(state.isBalanceVisible)
        assertNull(state.error)
    }

    @Test
    fun `copy - toggle visibility saldo bekerja dengan benar`() {
        val state = DashboardState()
        val toggled = state.copy(isBalanceVisible = !state.isBalanceVisible)

        assertFalse(toggled.isBalanceVisible)
    }

    @Test
    fun `copy - error dan loading dapat dimutasi`() {
        val state = DashboardState()
        val errorText = UiText.Raw("Koneksi gagal")
        val updated = state.copy(isLoading = false, error = errorText)

        assertFalse(updated.isLoading)
        assertEquals(errorText, updated.error)
    }
}
