package com.example.nexcashapplication.feature.settings.presentation.currency

import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencySettingsViewModel @Inject constructor() :
    BaseViewModel<CurrencySettingsState, CurrencySettingsEvent, CurrencySettingsEffect>(CurrencySettingsState()) {

    override fun onEvent(event: CurrencySettingsEvent) {
        when (event) {
            CurrencySettingsEvent.Refresh -> {

            }

            is CurrencySettingsEvent.OnMenuItemClick -> {
                viewModelScope.launch {
                    sendEffect(CurrencySettingsEffect.NavigateTo(event.destination))
                }
            }
        }
    }
}
