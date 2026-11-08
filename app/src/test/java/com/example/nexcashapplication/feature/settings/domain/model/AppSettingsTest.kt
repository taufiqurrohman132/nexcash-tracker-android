package com.example.nexcashapplication.feature.settings.domain.model

import com.example.nexcashapplication.core.domain.model.AppLanguage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AppSettingsTest {

    @Test
    fun `default values sesuai kontrak domain`() {
        val settings = AppSettings()

        assertEquals(AppThemeMode.SYSTEM, settings.themeMode)
        assertEquals("", settings.color)
        assertEquals(AppLanguage.INDONESIA, settings.language)
        assertEquals("IDR", settings.currency)
        assertTrue(settings.notificationMasterEnabled)
    }

    @Test
    fun `copy mengupdate nilai dengan benar`() {
        val original = AppSettings()
        val updated = original.copy(
            themeMode = AppThemeMode.DARK,
            color = "#123456",
            language = AppLanguage.ENGLISH,
            currency = "USD",
            notificationMasterEnabled = false,
        )

        assertEquals(AppThemeMode.DARK, updated.themeMode)
        assertEquals("#123456", updated.color)
        assertEquals(AppLanguage.ENGLISH, updated.language)
        assertEquals("USD", updated.currency)
        assertEquals(false, updated.notificationMasterEnabled)
    }
}
