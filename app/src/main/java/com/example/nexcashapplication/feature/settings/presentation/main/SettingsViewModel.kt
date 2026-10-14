package com.example.nexcashapplication.feature.settings.presentation.main

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.i18n.toUiText
import com.example.nexcashapplication.feature.settings.domain.model.AppThemeMode
import com.example.nexcashapplication.feature.settings.domain.repository.SettingsRepository
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations
import com.example.nexcashapplication.ui.theme.toComposeColor
import com.example.nexcashapplication.ui.theme.toHex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SettingsVm"

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : BaseViewModel<SettingsState, SettingsEvent, SettingsEffect>(SettingsState()) {

    init {
        observerSettings()
    }

    override fun onEvent(event: SettingsEvent) {
        when (event) {
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

            is SettingsEvent.OnColorSelected -> saveColor(event.color)

            is SettingsEvent.OnThemeSelected -> saveTheme(event.mode)
        }
    }

    /**
     * Simpan warna aksen. **Hanya menulis ke repository** — state tidak diset manual,
     * karena [observerSettings] akan mengantarkan nilai yang sama kembali dari DataStore.
     * Satu sumber kebenaran, sama seperti `language` dan `currency` yang memang sudah
     * bekerja begitu.
     *
     * Dulu cabang ini cuma `updateState { it.copy(color = ...) }`, jadi pilihan warna
     * hilang begitu aplikasi ditutup.
     */
    private fun saveColor(color: Color) {
        viewModelScope.launch {
            runCancellable { settingsRepository.updateColor(color.toHex()) }
                    .onFailure { e ->
                        Log.e(TAG, "gagal menyimpan warna tema", e)
                        sendEffect(
                            SettingsEffect.ShowSnackbar(e.toUiText(R.string.settings_save_failed)),
                        )
                    }
        }
    }

    private fun saveTheme(mode: AppThemeMode) {
        viewModelScope.launch {
            runCancellable { settingsRepository.updateTheme(mode) }
                    .onFailure { e ->
                        Log.e(TAG, "gagal menyimpan mode tema", e)
                        sendEffect(
                            SettingsEffect.ShowSnackbar(e.toUiText(R.string.settings_save_failed)),
                        )
                    }
        }
    }

    private fun observerSettings() {
        viewModelScope.launch {
            settingsRepository.settings
                    // DataStore gagal baca (file korup / disk penuh) → jangan crash;
                    // layar tetap memakai nilai default state dan tetap utuh.
                    .catch { e ->
                        Log.e(TAG, "gagal membaca pengaturan aplikasi", e)
                    }
                    .collect { appSettings ->
                        updateState {
                            it.copy(
                                color = appSettings.color.toComposeColor(),
                                themeMode = appSettings.themeMode,
                                language = appSettings.language.code,
                                currency = appSettings.currency,
                            )
                        }
                    }
        }
    }
}
