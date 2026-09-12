package com.example.nexcashapplication.feature.settings.presentation.currency

data class CurrencySettingsState(
    val isLoading: Boolean = false,
    val language: String = "Bahasa (IDR)",
    val themeColors: List<String> = emptyList(),                 // hex strings, konsisten pola String-for-color kamu
    val notificationSummary: String = "",                        // "Saldo, Pengingat Budget"
    val appVersion: String = "",
    val error: String? = null,
)