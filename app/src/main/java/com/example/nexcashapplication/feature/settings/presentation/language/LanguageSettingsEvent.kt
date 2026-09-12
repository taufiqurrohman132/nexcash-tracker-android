package com.example.nexcashapplication.feature.settings.presentation.language

import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations

sealed interface LanguageSettingsEvent {
    data object Refresh : LanguageSettingsEvent
    data class OnMenuItemClick(val destination: SettingsDestinations) : LanguageSettingsEvent
}