package com.example.nexcashapplication.feature.settings.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Unit test [AppThemeMode].
 *
 * Nilai mentah yang dikenal harus dipetakan persis; nilai tak dikenal — file DataStore
 * versi lama atau enum yang bertambah — jatuh ke [AppThemeMode.SYSTEM], bukan melempar.
 */
class AppThemeModeTest {

    @Test
    fun `nilai yang dikenal dipetakan persis`() {
        assertEquals(AppThemeMode.LIGHT, AppThemeMode.from("light"))
        assertEquals(AppThemeMode.DARK, AppThemeMode.from("dark"))
        assertEquals(AppThemeMode.SYSTEM, AppThemeMode.from("system"))
    }

    @Test
    fun `nilai tak dikenal jatuh ke SYSTEM bukan crash`() {
        // "tes" bukan kebetulan: nilai ini pernah dibandingkan secara harfiah di
        // SettingsRepositoryImpl (themeLight = it.themeMode == "tes") dan menghasilkan
        // false terus-menerus.
        assertEquals(AppThemeMode.SYSTEM, AppThemeMode.from(""))
        assertEquals(AppThemeMode.SYSTEM, AppThemeMode.from("tes"))
        assertEquals(AppThemeMode.SYSTEM, AppThemeMode.from("LIGHT")) // huruf besar bukan formatnya
    }

    @Test
    fun `setiap mode membulat balik ke nilai yang disimpan di DataStore`() {
        AppThemeMode.entries.forEach { mode ->
            assertEquals(mode, AppThemeMode.from(mode.raw))
        }
    }
}
