package com.example.nexcashapplication.feature.settings.presentation.main

import androidx.compose.ui.graphics.Color

data class SettingsState(
    val isLoading: Boolean = false,
    val language: String = "Bahasa (IDR)",
    val currency: String = " (IDR)",
    val colors: List<Color> = emptyList(),                 // hex strings, konsisten pola String-for-color kamu
    val notificationSummary: String = "",                        // "Saldo, Pengingat Budget"
    val appVersion: String = "",
    val error: String? = null,
)