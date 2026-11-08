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
    initialLanguage: AppLanguage = AppLanguage.INDONESIA,
    initialThemeMode: AppThemeMode = AppThemeMode.SYSTEM,
    initialColor: String = "",
    initialNotificationMasterEnabled: Boolean = true,
) : SettingsRepository {

    private val _settings = MutableStateFlow(
        AppSettings(
            themeMode = initialThemeMode,
            color = initialColor,
            language = initialLanguage,
            currency = initialCurrency,
            notificationMasterEnabled = initialNotificationMasterEnabled,
        ),
    )

    var customSettingsFlow: Flow<AppSettings>? = null

    override val settings: Flow<AppSettings>
        get() = customSettingsFlow ?: _settings.asStateFlow()

    /** Kode mata uang terakhir yang benar-benar disimpan, null kalau belum pernah. */
    var savedCurrency: String? = null
        private set

    var savedThemeMode: AppThemeMode? = null
        private set

    var savedColor: String? = null
        private set

    var savedLanguage: String? = null
        private set

    var savedNotificationMasterEnabled: Boolean? = null
        private set

    var shouldThrowOnUpdate: Boolean = false
    var errorToThrow: Throwable = IllegalStateException("Simulasi error persistensi settings")

    override suspend fun updateTheme(mode: AppThemeMode) {
        if (shouldThrowOnUpdate) throw errorToThrow
        savedThemeMode = mode
        _settings.value = _settings.value.copy(themeMode = mode)
    }

    override suspend fun updateColor(color: String) {
        if (shouldThrowOnUpdate) throw errorToThrow
        savedColor = color
        _settings.value = _settings.value.copy(color = color)
    }

    override suspend fun updateLanguage(languageId: String) {
        if (shouldThrowOnUpdate) throw errorToThrow
        savedLanguage = languageId
        _settings.value = _settings.value.copy(language = AppLanguage.fromCode(languageId))
    }

    override suspend fun updateCurrency(currency: String) {
        if (shouldThrowOnUpdate) throw errorToThrow
        savedCurrency = currency
        _settings.value = _settings.value.copy(currency = currency)
    }

    override suspend fun updateNotificationEnabled(enabled: Boolean) {
        if (shouldThrowOnUpdate) throw errorToThrow
        savedNotificationMasterEnabled = enabled
        _settings.value = _settings.value.copy(notificationMasterEnabled = enabled)
    }
}
