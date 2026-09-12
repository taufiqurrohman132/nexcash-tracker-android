package com.example.nexcashapplication.feature.settings.presentation.main

import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations

sealed interface SettingsEvent {
    data object Refresh : SettingsEvent
    data class OnMenuItemClick(val destination: SettingsDestinations) : SettingsEvent
}