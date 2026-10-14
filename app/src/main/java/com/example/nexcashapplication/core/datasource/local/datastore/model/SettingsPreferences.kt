package com.example.nexcashapplication.core.datasource.local.datastore.model

data class SettingsPreferences(
    val themeMode: String,
    /**
     * Warna aksen terpilih sebagai hex (mis. `#003DFF`). String kosong = belum pernah
     * dipilih, dan presentation-lah yang menentukan warna bawaannya.
     *
     * Kosong, bukan `PrimaryCyan`, supaya lapisan datastore ini tidak perlu mengimpor
     * `ui/theme` — keputusan soal warna bawaan milik presentation, bukan datasource.
     */
    val color: String,
    val languageCode: String,
    val currency: String,
    val notificationMasterEnabled: Boolean,
)
