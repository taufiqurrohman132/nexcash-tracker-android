package com.example.nexcashapplication.feature.notification.presentation.main

import com.example.nexcashapplication.core.i18n.UiText
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class NotificationStateTest {

    @Test
    fun `default values NotificationState sesuai spesifikasi awal`() {
        val state = NotificationState()

        assertTrue(state.groups.isEmpty())
        assertEquals(0, state.unreadCount)
        assertTrue(state.readIds.isEmpty())
        assertTrue(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `copy NotificationState memperbarui status error dan readIds`() {
        val state = NotificationState()
        val error = UiText.Raw("Koneksi gagal")
        val updated = state.copy(
            isLoading = false,
            error = error,
            readIds = setOf("id-1"),
            unreadCount = 2,
        )

        assertFalse(updated.isLoading)
        assertEquals(error, updated.error)
        assertEquals(setOf("id-1"), updated.readIds)
        assertEquals(2, updated.unreadCount)
    }
}
