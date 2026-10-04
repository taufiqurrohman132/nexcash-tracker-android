package com.example.nexcashapplication.feature.settings.presentation.currency

/**
 * Layar Mata Uang tidak punya tujuan navigasi selain kembali, jadi tidak ada
 * [NavigateTo] seperti di layar Settings/Language. Menghapusnya sekaligus
 * menghapus enam cabang `TODO()` yang dulunya menunggu di screen.
 */
sealed interface CurrencySettingsEffect {
    data object NavigateBack : CurrencySettingsEffect

    data class ShowSnackbar(val message: String) : CurrencySettingsEffect
}
