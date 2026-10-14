package com.example.nexcashapplication.feature.settings.presentation.main

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.core.domain.model.AppCurrency
import com.example.nexcashapplication.core.domain.model.AppLanguage
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.feature.settings.domain.model.AppThemeMode
import com.example.nexcashapplication.ui.theme.PrimaryCyan

data class SettingsState(
    val isLoading: Boolean = false,

    /** Kode bahasa aktif — nama tampilnya di-resolve dari `settings_lang_*` di layar. */
    val language: String = AppLanguage.INDONESIA.code,

    /** Kode mata uang aktif — nama & kode tampilnya di-resolve dari `AppCurrency` di layar. */
    val currency: String = AppCurrency.IDR.code,

    /**
     * Warna aksen terpilih — sudah di-resolve dari hex, jadi siap dipakai sebagai
     * seed color tema maupun sebagai pratinjau di baris "Tema & Warna".
     */
    val color: Color = PrimaryCyan,

    /**
     * Mode tema terpilih. Nilai ini bukan untuk dirender di layar Settings —
     * `MainActivity` yang membacanya langsung dari repository untuk mengubah tema.
     */
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,

    val notificationSummary: String = "",                        // "Saldo, Pengingat Budget"
    val appVersion: String = "",
    /** Gagal memuat pengaturan; null = tidak ada error. */
    val error: UiText? = null,

    val showThemeColorSheet: Boolean = false
)
