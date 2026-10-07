package com.example.nexcashapplication.feature.settings.presentation.language

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.domain.model.AppLanguage
import com.example.nexcashapplication.core.i18n.AppLocale
import com.example.nexcashapplication.feature.settings.domain.repository.SettingsRepository
import com.example.nexcashapplication.feature.settings.presentation.language.model.LanguageItemUiModel
import com.example.nexcashapplication.feature.settings.presentation.language.model.nameResId
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
                        LanguageSettingsEffect.Saved(state.value.selectedLanguageId),
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
                                        nameRes = language.nameResId,
                                    )
                                },
                            )
                        }
                    }
        }

    }

    /**
     * Simpan pilihan ke DataStore lalu terapkan sekarang juga. [AppLocale.apply] mengganti
     * Locale default JVM (`java.time`, `NumberFormat`); resources Activity baru berubah setelah
     * Activity di-recreate oleh penerima [LanguageSettingsEffect.Saved]. Sengaja suspend —
     * efek boleh dikirim hanya setelah persistensi selesai.
     */
    private suspend fun handleSave(selectedLanguageId: String) {
        settingsRepository.updateLanguage(selectedLanguageId)
        AppLocale.apply(selectedLanguageId)
    }

    companion object {
        private const val TAG = "LanguageSettingsViewMo"
    }
}
