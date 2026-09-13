package com.example.nexcashapplication.feature.settings.domain.model

import com.example.nexcashapplication.core.domain.model.AppLanguage

data class AppSettings(
    val themeLight: Boolean = true,
    val color: String = "",
    val language: AppLanguage = AppLanguage.INDONESIA,
    val currency: String = "",
    val notificationMasterEnabled: Boolean = true,
)
