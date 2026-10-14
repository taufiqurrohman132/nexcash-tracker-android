package com.example.nexcashapplication.feature.settings.presentation.language

import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations

sealed interface LanguageSettingsEvent {
    object OnSearchClear : LanguageSettingsEvent
    object OnBackClick : LanguageSettingsEvent
    object OnSaveClick : LanguageSettingsEvent
    data class OnItemClick(val itemId: String) : LanguageSettingsEvent
    data class OnSearchQueryChange(val query: String) : LanguageSettingsEvent
}