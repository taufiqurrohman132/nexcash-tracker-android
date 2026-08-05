package com.example.nexcashapplication.feature.settings.presentation.viewmodel

import com.example.nexcashapplication.core.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class SettingsState(
    val isLoading: Boolean = false
)

sealed interface SettingsEvent {
    data object Refresh : SettingsEvent
}

@HiltViewModel
class SettingsViewModel @Inject constructor() : 
    BaseViewModel<SettingsState, SettingsEvent, Unit>(SettingsState()) {

    override fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.Refresh -> {
                // TODO
            }
        }
    }
}
