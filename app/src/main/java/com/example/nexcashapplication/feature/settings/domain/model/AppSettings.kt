package com.example.nexcashapplication.feature.settings.domain.model

import com.example.nexcashapplication.core.domain.model.AppLanguage

data class AppSettings(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,

    /**
     * Warna aksen terpilih sebagai hex (mis. `#003DFF`). String kosong = belum pernah
     * memilih, dan presentation-lah yang menerjemahkannya ke warna bawaan.
     */
    val color: String = "",
    val language: AppLanguage = AppLanguage.INDONESIA,
    val currency: String = "IDR",
    val notificationMasterEnabled: Boolean = true,
)
