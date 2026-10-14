package com.example.nexcashapplication.feature.settings.domain.model

/**
 * Mode tema aplikasi.
 *
 * [SYSTEM] adalah bawaan: ikut tema perangkat. [LIGHT] dan [DARK] memaksa salah satu.
 *
 * Disimpan sebagai [raw] di DataStore supaya nilainya tetap terbaca walau daftar enum
 * bertambah — konsisten dengan pola
 * [com.example.nexcashapplication.core.domain.model.AppLanguage].
 *
 * Model ini sebelumnya hanya `themeLight: Boolean`, yang tidak punya tempat untuk mode
 * "ikut sistem" dan memaksa `SettingsRepositoryImpl` membandingkan string mentah
 * (`themeMode == "tes"`) untuk menebak maknanya.
 */
enum class AppThemeMode(val raw: String) {
    LIGHT("light"),
    DARK("dark"),
    SYSTEM("system");

    companion object {

        /**
         * Nilai tak dikenal — file DataStore versi lama atau enum yang bertambah —
         * jatuh ke [SYSTEM], bukan melempar. Tema yang salah lebih baik daripada crash.
         */
        fun from(raw: String): AppThemeMode =
            entries.firstOrNull { it.raw == raw } ?: SYSTEM
    }
}
