package com.example.nexcashapplication.core.domain.model

import androidx.annotation.StringRes
import com.example.nexcashapplication.R

/**
 * Mata uang yang bisa dipilih di layar pengaturan Mata Uang.
 *
 * [displayNameRes] adalah satu-satunya sumber **tampilan** — teksnya di-resolve lewat
 * `stringResource(...)` di layar, jadi ikut berbahasa. [displayName] hanya kunci pencarian
 * (dan dipakai unit test); simbol mata uang (`Rp`, `US$`, ...) bukan terjemahan sehingga
 * dibiarkan apa adanya.
 */
enum class AppCurrency(
    val code: String,
    val displayName: String,
    val symbol: String,
    @StringRes val displayNameRes: Int,
) {
    IDR("IDR", "Rupiah Indonesia", "Rp", R.string.settings_currency_idr),
    USD("USD", "Dolar Amerika Serikat", "$", R.string.settings_currency_usd),
    EUR("EUR", "Euro", "€", R.string.settings_currency_eur),
    SGD("SGD", "Dolar Singapura", "S$", R.string.settings_currency_sgd),
    JPY("JPY", "Yen Jepang", "¥", R.string.settings_currency_jpy),
    MYR("MYR", "Ringgit Malaysia", "RM", R.string.settings_currency_myr),
    GBP("GBP", "Poundsterling Inggris", "£", R.string.settings_currency_gbp),
    AUD("AUD", "Dolar Australia", "A$", R.string.settings_currency_aud);

    companion object {
        fun fromCode(code: String): AppCurrency = entries.find { it.code == code } ?: IDR
    }
}
