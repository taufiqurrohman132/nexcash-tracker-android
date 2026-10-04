package com.example.nexcashapplication.feature.settings.presentation.currency

import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.domain.model.AppCurrency
import com.example.nexcashapplication.feature.settings.domain.repository.SettingsRepository
import com.example.nexcashapplication.feature.settings.presentation.currency.model.CurrencyItemUiModel
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
class CurrencySettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : BaseViewModel<CurrencySettingsState, CurrencySettingsEvent, CurrencySettingsEffect>(
    CurrencySettingsState(),
) {

    init {
        observerCurrency()
    }

    override fun onEvent(event: CurrencySettingsEvent) {
        when (event) {
            CurrencySettingsEvent.Refresh -> {
            }

            is CurrencySettingsEvent.OnItemClick -> {
                updateState {
                    it.copy(selectedCurrencyId = event.itemId)
                }
            }

            CurrencySettingsEvent.OnSearchClear -> {
                updateState {
                    it.copy(searchQuery = "")
                }
            }

            is CurrencySettingsEvent.OnSearchQueryChange -> {
                updateState {
                    it.copy(searchQuery = event.query)
                }
            }

            CurrencySettingsEvent.OnBackClick -> viewModelScope.launch {
                sendEffect(CurrencySettingsEffect.NavigateBack)
            }

            CurrencySettingsEvent.OnSaveClick -> viewModelScope.launch {
                settingsRepository.updateCurrency(state.value.selectedCurrencyId)
                sendEffect(CurrencySettingsEffect.NavigateBack)
            }
        }
    }

    /**
     * Mengisi pilihan awal dari preferensi tersimpan, lalu memfilter daftar
     * mata uang mengikuti [CurrencySettingsState.searchQuery].
     *
     * Pilihan awal direset bersama [CurrencySettingsState.initialSelectedCurrencyId]
     * supaya tombol Simpan baru menyala kalau pengguna benar-benar mengganti.
     */
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun observerCurrency() {
        viewModelScope.launch {
            settingsRepository.settings.collect { appSettings ->
                val currentCode = AppCurrency.fromCode(appSettings.currency).code
                updateState {
                    it.copy(
                        selectedCurrencyId = currentCode,
                        initialSelectedCurrencyId = currentCode,
                    )
                }
            }
        }

        viewModelScope.launch {
            state.map { it.searchQuery }
                .distinctUntilChanged()
                .debounce(300.milliseconds)
                .flatMapLatest { query ->
                    flowOf(
                        if (query.isBlank()) {
                            AppCurrency.entries
                        } else {
                            AppCurrency.entries.filter { currency ->
                                currency.displayName.contains(query, ignoreCase = true) ||
                                        currency.code.contains(query, ignoreCase = true)
                            }
                        },
                    )
                }
                .collect { listCurrency ->
                    updateState {
                        it.copy(
                            listCurrency = listCurrency.map { currency ->
                                CurrencyItemUiModel(
                                    id = currency.code,
                                    name = currency.displayName,
                                    symbol = currency.symbol,
                                )
                            },
                        )
                    }
                }
        }
    }
}
