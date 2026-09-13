package com.example.nexcashapplication.core.datasource.local.datastore.model

data class SettingsPreferences(
    val themeMode: String,
    val languageCode: String,
    val currency: String,
    val notificationMasterEnabled: Boolean,
)
