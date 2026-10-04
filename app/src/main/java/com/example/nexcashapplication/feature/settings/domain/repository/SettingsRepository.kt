package com.example.nexcashapplication.feature.settings.domain.repository

import com.example.nexcashapplication.feature.settings.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>
    suspend fun updateTheme(mode: String)
    suspend fun updateColor(color: String)
    suspend fun updateLanguage(languageId: String)
    suspend fun updateCurrency(currency: String)

    /** Saklar master notifikasi (pengingat anggaran) — bukan preferensi per budget. */
    suspend fun updateNotificationEnabled(enabled: Boolean)
}