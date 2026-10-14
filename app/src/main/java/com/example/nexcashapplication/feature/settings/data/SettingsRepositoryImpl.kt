package com.example.nexcashapplication.feature.settings.data

import com.example.nexcashapplication.core.datasource.local.datastore.SettingsPreferencesDataSource
import com.example.nexcashapplication.core.domain.model.AppLanguage
import com.example.nexcashapplication.feature.settings.domain.model.AppSettings
import com.example.nexcashapplication.feature.settings.domain.model.AppThemeMode
import com.example.nexcashapplication.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsPreferencesDataSource: SettingsPreferencesDataSource,
) : SettingsRepository {

    override val settings: Flow<AppSettings> =
        settingsPreferencesDataSource.settingsPreferences.map {
            AppSettings(
                // Dulu `themeLight = it.themeMode == "tes"` — membandingkan dengan literal
                // "tes" yang tak pernah jadi nilai valid, jadi hasilnya selalu false.
                // Sekarang dipetakan lewat enum, dan nilai tak dikenal jatuh ke SYSTEM.
                themeMode = AppThemeMode.from(it.themeMode),
                color = it.color,
                language = AppLanguage.fromCode(it.languageCode.lowercase()),
                currency = it.currency,
                notificationMasterEnabled = it.notificationMasterEnabled,
            )
        }

    override suspend fun updateTheme(mode: AppThemeMode) {
        settingsPreferencesDataSource.setThemeMode(mode.raw)
    }

    override suspend fun updateColor(color: String) {
        settingsPreferencesDataSource.setColor(color)
    }

    override suspend fun updateLanguage(languageId: String) {
        settingsPreferencesDataSource.setLanguage(languageId)
    }

    override suspend fun updateCurrency(currency: String) {
        settingsPreferencesDataSource.setCurrency(currency)
    }

    override suspend fun updateNotificationEnabled(enabled: Boolean) {
        settingsPreferencesDataSource.setNotificationMasterEnabled(enabled)
    }
}
