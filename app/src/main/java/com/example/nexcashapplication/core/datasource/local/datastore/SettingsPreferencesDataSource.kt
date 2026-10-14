package com.example.nexcashapplication.core.datasource.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.nexcashapplication.core.datasource.local.datastore.model.SettingsPreferences
import com.example.nexcashapplication.core.domain.model.AppLanguage
import com.example.nexcashapplication.di.SettingsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject

class SettingsPreferencesDataSource @Inject constructor(
    @param:SettingsDataStore private val dataStore: DataStore<Preferences>,
) {

    private object Keys {
        val THEME = stringPreferencesKey("theme_color")
        val COLOR = stringPreferencesKey("accent_color")
        val LANGUAGE = stringPreferencesKey("language")
        val CURRENCY = stringPreferencesKey("currency")
        val NOTIFICATION_MASTER_ENABLED = booleanPreferencesKey("notification_master_enabled")
    }

    val settingsPreferences: Flow<SettingsPreferences> = dataStore.data.map {
        SettingsPreferences(
            themeMode = it[Keys.THEME] ?: "system",
            // Kosong kalau belum pernah memilih warna; presentation yang menentukan
            // warna bawaannya (PrimaryCyan) — lihat `String.toComposeColor()`.
            color = it[Keys.COLOR] ?: "",
            // Bahasa pertama kali: ikut bahasa perangkat kalau didukung, kalau tidak
            // jatuh ke default aplikasi (Bahasa Indonesia, isi dari res/values).
            languageCode = it[Keys.LANGUAGE] ?: defaultLanguageCode(),
            currency = it[Keys.CURRENCY] ?: "IDR",
            notificationMasterEnabled = it[Keys.NOTIFICATION_MASTER_ENABLED] ?: true,
        )
    }

    suspend fun setThemeMode(mode: String) = dataStore.edit { it[Keys.THEME] = mode }

    suspend fun setColor(color: String) = dataStore.edit { it[Keys.COLOR] = color }

    suspend fun setLanguage(language: String) = dataStore.edit { it[Keys.LANGUAGE] = language }

    suspend fun setCurrency(currency: String) = dataStore.edit { it[Keys.CURRENCY] = currency }

    suspend fun setNotificationMasterEnabled(enabled: Boolean) =
        dataStore.edit { it[Keys.NOTIFICATION_MASTER_ENABLED] = enabled }

    /**
     * Bahasa saat pengguna belum pernah memilih: ikut bahasa perangkat kalau didukung
     * aplikasi, selain itu Bahasa Indonesia (default res/values).
     */
    private fun defaultLanguageCode(): String {
        val device = Locale.getDefault().language
        return if (AppLanguage.entries.any { it.code == device }) device
        else AppLanguage.INDONESIA.code
    }

}