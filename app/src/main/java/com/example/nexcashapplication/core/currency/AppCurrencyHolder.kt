package com.example.nexcashapplication.core.currency

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.nexcashapplication.core.domain.model.AppCurrency

/**
 * Mata uang aktif untuk format tampilan, mengikuti pilihan di layar pengaturan Mata Uang.
 *
 * Dipakai oleh extension formatter (lihat `CurrencyExtension`) dan composable — sengaja
 * berupa singleton seperti [com.example.nexcashapplication.core.i18n.AppLocale], karena
 * kode non-UI (mapper, state) tidak membawa `Context` dan extension function tidak bisa
 * menerima dependensi ter-inject.
 *
 * Disimpan sebagai `mutableStateOf` (bukan StateFlow) supaya pembacaan `value` dari
 * composable ikut berlangganan di snapshot — ganti mata uang → layar yang menampilkan
 * nominal ikut recompose tanpa `collectAsState` di tiap call site. Tulisan dari thread
 * mana pun aman (di-apply ke global snapshot).
 *
 * Diisi dari DataStore di `MyApps.onCreate` (blocking sekali, lalu collect Flow supaya
 * perubahan dari `CurrencySettingsViewModel` langsung terlihat tanpa perlu recreate).
 */
object AppCurrencyHolder {

    private var state by mutableStateOf(AppCurrency.IDR)

    /** Nilai saat ini — aman dibaca dari thread mana pun. */
    val value: AppCurrency
        get() = state

    fun set(code: String) {
        state = AppCurrency.fromCode(code)
    }
}
