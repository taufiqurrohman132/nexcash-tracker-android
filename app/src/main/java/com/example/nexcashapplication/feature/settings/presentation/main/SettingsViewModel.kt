package com.example.nexcashapplication.feature.settings.presentation.main

import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.feature.settings.domain.repository.SettingsRepository
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations
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
                // Tema & Warna membuka bottom sheet di tempat, bukan pindah layar.
                // Tanpa cabang ini showThemeColorSheet tak pernah jadi true dan
                // sheet-nya tidak pernah muncul.
                if (event.destination == SettingsDestinations.TEMA_WARNA) {
                    updateState { it.copy(showThemeColorSheet = true) }
                } else {
                    viewModelScope.launch {
                        sendEffect(SettingsEffect.NavigateTo(event.destination))
                    }
                }
            }

            SettingsEvent.OnThemeSheetDismiss -> {
                updateState { it.copy(showThemeColorSheet = false) }
            }

            is SettingsEvent.OnColorSelected -> {
                updateState { it.copy(color = event.color) }
            }
        }
    }

    private fun observerSettings() {
        viewModelScope.launch {
            settingsRepository.settings.collect { appSettings ->
                updateState {
                    it.copy(
//                        colors = appSettings.color,
                        language = appSettings.language.code,
                        currency = appSettings.currency,
                    )
                }
            }
        }
    }
}
