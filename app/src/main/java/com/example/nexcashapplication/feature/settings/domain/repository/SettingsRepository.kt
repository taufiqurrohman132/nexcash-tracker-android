package com.example.nexcashapplication.feature.settings.domain.repository

import com.example.nexcashapplication.feature.settings.domain.model.AppSettings
import com.example.nexcashapplication.feature.settings.domain.model.AppThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>
    suspend fun updateTheme(mode: AppThemeMode)

    /** [color] berupa hex `#RRGGBB`; kosong berarti "kembali ke warna bawaan". */
    suspend fun updateColor(color: String)
    suspend fun updateLanguage(languageId: String)
    suspend fun updateCurrency(currency: String)

    /** Saklar master notifikasi (pengingat anggaran) — bukan preferensi per budget. */
    suspend fun updateNotificationEnabled(enabled: Boolean)
}
