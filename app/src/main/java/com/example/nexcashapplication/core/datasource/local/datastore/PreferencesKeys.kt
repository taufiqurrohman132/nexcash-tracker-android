package com.example.nexcashapplication.core.datasource.local.datastore

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val RECENT_COLORS = stringPreferencesKey("recent_colors")
    val RECENT_ICONS = stringPreferencesKey("recent_icons")
}