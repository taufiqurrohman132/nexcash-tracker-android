package com.example.nexcashapplication.core.domain.model

/**
 * Mata uang yang bisa dipilih di layar pengaturan Mata Uang.
 *
 * Nama tampilan ditulis langsung dalam Bahasa Indonesia, bukan diturunkan dari
 * [java.util.Currency.getDisplayName]. Kalau diturunkan dari Locale, nama tiap
 * baris akan berbahasa masing-masing ("Rupiah Indonesia", "US Dollar", "Euro")
 * sehingga daftarnya campur bahasa.
 */
enum class AppCurrency(
    val code: String,
    val displayName: String,
    val symbol: String,
) {
    IDR("IDR", "Rupiah Indonesia", "Rp"),
    USD("USD", "Dolar Amerika Serikat", "$"),
    EUR("EUR", "Euro", "€"),
    SGD("SGD", "Dolar Singapura", "S$"),
    JPY("JPY", "Yen Jepang", "¥"),
    MYR("MYR", "Ringgit Malaysia", "RM"),
    GBP("GBP", "Poundsterling Inggris", "£"),
    AUD("AUD", "Dolar Australia", "A$");

    companion object {
        fun fromCode(code: String): AppCurrency = entries.find { it.code == code } ?: IDR
    }
}
