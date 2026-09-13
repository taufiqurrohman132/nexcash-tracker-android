package com.example.nexcashapplication.feature.settings.presentation.language

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.domain.model.AppLanguage
import com.example.nexcashapplication.feature.settings.domain.repository.SettingsRepository
import com.example.nexcashapplication.feature.settings.presentation.language.model.LanguageItemUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class LanguageSettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : BaseViewModel<LanguageSettingsState, LanguageSettingsEvent, LanguageSettingsEffect>(
    LanguageSettingsState(),
) {

    init {
        observerLanguage()
    }

    override fun onEvent(event: LanguageSettingsEvent) {
        when (event) {
            LanguageSettingsEvent.Refresh -> {

            }

            is LanguageSettingsEvent.OnItemClick -> {
                updateState {
                    it.copy(
                        selectedLanguageId = event.itemId,
                    )
                }
            }

            LanguageSettingsEvent.OnSearchClear -> {
                updateState {
                    it.copy(searchQuery = "")
                }
            }

            is LanguageSettingsEvent.OnSearchQueryChange -> {
                updateState {
                    it.copy(
                        searchQuery = event.query,
                    )
                }
            }

            LanguageSettingsEvent.OnBackClick -> viewModelScope.launch {
                sendEffect(
                    LanguageSettingsEffect.NavigateBack,
                )
            }

            LanguageSettingsEvent.OnSaveClick -> {
                viewModelScope.launch {
                    Log.d(TAG, "onEvent: selected lacnguage = ${state.value.selectedLanguageId}")
                    handleSave(state.value.selectedLanguageId)
                    sendEffect(
                        LanguageSettingsEffect.NavigateBack,
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    fun observerLanguage() {
        viewModelScope.launch {
            settingsRepository.settings.collect { appSettings ->
                updateState {
                    it.copy(
                        selectedLanguageId = appSettings.language.code,
                        initialSelectedLanguageId = appSettings.language.code,
                    )
                }
            }
        }

        viewModelScope.launch {
            state.map { it.searchQuery }
                    .distinctUntilChanged()
                    .debounce(300.milliseconds)
                    .flatMapLatest {
                        if (it.isBlank()) {
                            flowOf(AppLanguage.entries)
                        } else {
                            flowOf(
                                AppLanguage.entries.filter { language ->
                                    language.name.contains(it, ignoreCase = true)
                                },
                            )
                        }
                    }
                    .collect { listLanguage ->
                        updateState {
                            it.copy(
                                listLanguage = listLanguage.map { language ->
                                    LanguageItemUiModel(
                                        id = language.code,
                                        name = language.displayNmae,
                                    )
                                },
                            )
                        }
                    }
        }

    }

    private fun handleSave(selectedLanguageId: String) {
        viewModelScope.launch {
            settingsRepository.updateLanguage(selectedLanguageId)
        }
    }

    companion object {
        private const val TAG = "LanguageSettingsViewMo"
    }
}
