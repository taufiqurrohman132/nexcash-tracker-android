package com.example.nexcashapplication.feature.settings.presentation.currency

import com.example.nexcashapplication.core.domain.model.AppLanguage
import com.example.nexcashapplication.feature.settings.domain.model.AppSettings
import com.example.nexcashapplication.feature.settings.domain.model.AppThemeMode
import com.example.nexcashapplication.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** [SettingsRepository] tiruan — hanya preferensi yang benar-benar diuji. */
class FakeSettingsRepository(
    initialCurrency: String = "IDR",
) : SettingsRepository {

    private val _settings = MutableStateFlow(AppSettings(currency = initialCurrency))

    override val settings: Flow<AppSettings> = _settings.asStateFlow()

    /** Kode mata uang terakhir yang benar-benar disimpan, null kalau belum pernah. */
    var savedCurrency: String? = null
        private set

    override suspend fun updateTheme(mode: AppThemeMode) {
        _settings.value = _settings.value.copy(themeMode = mode)
    }

    override suspend fun updateColor(color: String) {
        _settings.value = _settings.value.copy(color = color)
    }

    override suspend fun updateLanguage(languageId: String) {
        _settings.value = _settings.value.copy(language = AppLanguage.fromCode(languageId))
    }

    override suspend fun updateCurrency(currency: String) {
        savedCurrency = currency
        _settings.value = _settings.value.copy(currency = currency)
    }

    override suspend fun updateNotificationEnabled(enabled: Boolean) {
        _settings.value = _settings.value.copy(notificationMasterEnabled = enabled)
    }
}
