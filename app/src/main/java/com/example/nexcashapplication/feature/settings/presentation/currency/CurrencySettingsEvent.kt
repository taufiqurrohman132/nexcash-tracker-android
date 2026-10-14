package com.example.nexcashapplication.feature.settings.presentation.currency

sealed interface CurrencySettingsEvent {
    data object OnSearchClear : CurrencySettingsEvent
    data object OnBackClick : CurrencySettingsEvent
    data object OnSaveClick : CurrencySettingsEvent
    data class OnItemClick(val itemId: String) : CurrencySettingsEvent
    data class OnSearchQueryChange(val query: String) : CurrencySettingsEvent
}
