package com.example.nexcashapplication.feature.settings.presentation.main

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.feature.settings.domain.model.AppThemeMode
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations

sealed interface SettingsEvent {
    data class OnMenuItemClick(val destination: SettingsDestinations) : SettingsEvent

    /** Tutup bottom sheet pilih warna. */
    data object OnThemeSheetDismiss : SettingsEvent

    /** Pilih warna aplikasi — tampil sebagai pratinjau di baris "Tema & Warna". */
    data class OnColorSelected(val color: Color) : SettingsEvent

    /** Pilih mode tema: Terang / Gelap / Ikuti Sistem. */
    data class OnThemeSelected(val mode: AppThemeMode) : SettingsEvent
}
