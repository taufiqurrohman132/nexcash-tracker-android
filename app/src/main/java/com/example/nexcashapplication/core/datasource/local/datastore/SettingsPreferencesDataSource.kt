package com.example.nexcashapplication.core.datasource.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.nexcashapplication.core.datasource.local.datastore.model.SettingsPreferences
import com.example.nexcashapplication.di.SettingsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsPreferencesDataSource @Inject constructor(
    @param:SettingsDataStore private val dataStore: DataStore<Preferences>,
) {

    private object Keys {
        val THEME = stringPreferencesKey("theme_color")
        val LANGUAGE = stringPreferencesKey("language")
        val CURRENCY = stringPreferencesKey("currency")
        val NOTIFICATION_MASTER_ENABLED = booleanPreferencesKey("notification_master_enabled")
    }

    val settingsPreferences: Flow<SettingsPreferences> = dataStore.data.map {
        SettingsPreferences(
            themeMode = it[Keys.THEME] ?: "system",
            languageCode = it[Keys.LANGUAGE] ?: "en",
            currency = it[Keys.CURRENCY] ?: "USD",
            notificationMasterEnabled = it[Keys.NOTIFICATION_MASTER_ENABLED] ?: true,
        )
    }

    suspend fun setThemeMode(mode: String) = dataStore.edit { it[Keys.THEME] = mode }

    suspend fun setLanguage(language: String) = dataStore.edit { it[Keys.LANGUAGE] = language }

    suspend fun setCurrency(currency: String) = dataStore.edit { it[Keys.CURRENCY] = currency }

//    val notificationMasterEnabled: Flow<Boolean> =
//        dataStore.data.map { it[Keys.NOTIFICATION_MASTER_ENABLED] ?: true }


}