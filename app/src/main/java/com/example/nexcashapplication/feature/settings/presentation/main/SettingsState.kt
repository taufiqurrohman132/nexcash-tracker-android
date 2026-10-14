package com.example.nexcashapplication.feature.settings.presentation.main

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.core.domain.model.AppCurrency
import com.example.nexcashapplication.core.domain.model.AppLanguage
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.ui.theme.PrimaryCyan

data class SettingsState(
    val isLoading: Boolean = false,

    /** Kode bahasa aktif — nama tampilnya di-resolve dari `settings_lang_*` di layar. */
    val language: String = AppLanguage.INDONESIA.code,

    /** Kode mata uang aktif — nama & kode tampilnya di-resolve dari `AppCurrency` di layar. */
    val currency: String = AppCurrency.IDR.code,

    val color: Color = PrimaryCyan,                 // hex strings, konsisten pola String-for-color kamu
    val notificationSummary: String = "",                        // "Saldo, Pengingat Budget"
    val appVersion: String = "",
    /** Gagal memuat pengaturan; null = tidak ada error. */
    val error: UiText? = null,

    val showThemeColorSheet: Boolean = false
)
