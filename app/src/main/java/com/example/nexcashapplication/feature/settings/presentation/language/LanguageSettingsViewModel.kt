package com.example.nexcashapplication.feature.settings.presentation.language

import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageSettingsViewModel @Inject constructor() :
    BaseViewModel<LanguageSettingsState, LanguageSettingsEvent, LanguageSettingsEffect>(LanguageSettingsState()) {

    override fun onEvent(event: LanguageSettingsEvent) {
        when (event) {
            LanguageSettingsEvent.Refresh -> {

            }

            is LanguageSettingsEvent.OnMenuItemClick -> {
                viewModelScope.launch {
                    sendEffect(LanguageSettingsEffect.NavigateTo(event.destination))
                }
            }
        }
    }
}
