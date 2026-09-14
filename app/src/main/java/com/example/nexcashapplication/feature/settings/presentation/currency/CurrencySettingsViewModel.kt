package com.example.nexcashapplication.feature.settings.presentation.currency

import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.feature.settings.presentation.currency.CurrencySettingsEffect.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencySettingsViewModel @Inject constructor() :
    BaseViewModel<CurrencySettingsState, CurrencySettingsEvent, CurrencySettingsEffect>(
        CurrencySettingsState(),
    ) {

    override fun onEvent(event: CurrencySettingsEvent) {
        when (event) {
            CurrencySettingsEvent.Refresh -> {

            }

            is CurrencySettingsEvent.OnItemClick -> {
                viewModelScope.launch {
//                    sendEffect(NavigateTo(event.itemId))
                }
            }

            CurrencySettingsEvent.OnBackClick -> TODO()
            CurrencySettingsEvent.OnSaveClick -> TODO()
            CurrencySettingsEvent.OnSearchClear -> TODO()
            is CurrencySettingsEvent.OnSearchQueryChange -> TODO()
        }
    }
}
