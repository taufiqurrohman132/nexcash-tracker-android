package com.example.nexcashapplication.feature.settings.presentation.main

import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() :
    BaseViewModel<SettingsState, SettingsEvent, SettingsEffect>(SettingsState()) {

    override fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.Refresh -> {

            }

            is SettingsEvent.OnMenuItemClick -> {
                viewModelScope.launch {
                    sendEffect(SettingsEffect.NavigateTo(event.destination))
                }
            }
        }
    }
}
