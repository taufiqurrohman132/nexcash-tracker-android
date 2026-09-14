package com.example.nexcashapplication.feature.settings.presentation.currency

import com.example.nexcashapplication.feature.settings.presentation.language.LanguageSettingsEvent
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations

sealed interface CurrencySettingsEvent {
    data object Refresh : CurrencySettingsEvent
    object OnSearchClear : CurrencySettingsEvent
    object OnBackClick : CurrencySettingsEvent
    object OnSaveClick : CurrencySettingsEvent
    data class OnItemClick(val itemId: String) : CurrencySettingsEvent
    data class OnSearchQueryChange(val query: String) : CurrencySettingsEvent
}