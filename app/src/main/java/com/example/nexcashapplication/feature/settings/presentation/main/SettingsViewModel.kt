package com.example.nexcashapplication.feature.settings.presentation.main

import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.feature.settings.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : BaseViewModel<SettingsState, SettingsEvent, SettingsEffect>(SettingsState()) {

    init {
        observerSettings()
    }

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

    private fun observerSettings() {
        viewModelScope.launch {
            settingsRepository.settings.collect { appSettings ->
                updateState {
                    it.copy(
//                        colors = appSettings.color,
                        language = appSettings.language.displayNmae,
                        currency = appSettings.currency,
                    )
                }
            }
        }
    }
}
