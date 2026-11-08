package com.example.nexcashapplication.feature.settings.presentation.main

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.core.domain.model.AppCurrency
import com.example.nexcashapplication.core.domain.model.AppLanguage
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.feature.settings.domain.model.AppThemeMode
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SettingsStateTest {

    @Test
    fun `default values sesuai kontrak awal state`() {
        val state = SettingsState()

        assertFalse(state.isLoading)
        assertEquals(AppLanguage.INDONESIA.code, state.language)
        assertEquals(AppCurrency.IDR.code, state.currency)
        assertEquals(PrimaryCyan, state.color)
        assertEquals(AppThemeMode.SYSTEM, state.themeMode)
        assertEquals("", state.notificationSummary)
        assertEquals("", state.appVersion)
        assertNull(state.error)
        assertFalse(state.showThemeColorSheet)
    }

    @Test
    fun `copy state memperbarui properti dengan benar`() {
        val original = SettingsState()
        val customColor = Color.Magenta
        val updated = original.copy(
            isLoading = true,
            language = "en",
            currency = "USD",
            color = customColor,
            themeMode = AppThemeMode.DARK,
            notificationSummary = "Saldo, Pengingat",
            appVersion = "1.0.0",
            error = UiText.Raw("Error occurred"),
            showThemeColorSheet = true,
        )

        assertTrue(updated.isLoading)
        assertEquals("en", updated.language)
        assertEquals("USD", updated.currency)
        assertEquals(customColor, updated.color)
        assertEquals(AppThemeMode.DARK, updated.themeMode)
        assertEquals("Saldo, Pengingat", updated.notificationSummary)
        assertEquals("1.0.0", updated.appVersion)
        assertEquals(UiText.Raw("Error occurred"), updated.error)
        assertTrue(updated.showThemeColorSheet)
    }
}
