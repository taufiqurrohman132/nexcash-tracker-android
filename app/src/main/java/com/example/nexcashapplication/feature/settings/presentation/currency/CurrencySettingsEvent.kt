package com.example.nexcashapplication.feature.settings.presentation.currency

import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations

sealed interface CurrencySettingsEvent {
    data object Refresh : CurrencySettingsEvent
    data class OnMenuItemClick(val destination: SettingsDestinations) : CurrencySettingsEvent
}