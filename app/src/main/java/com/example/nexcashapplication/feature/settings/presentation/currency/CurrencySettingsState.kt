package com.example.nexcashapplication.feature.settings.presentation.currency

import com.example.nexcashapplication.core.domain.model.AppCurrency
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.feature.settings.presentation.currency.model.CurrencyItemUiModel

data class CurrencySettingsState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val listCurrency: List<CurrencyItemUiModel> = emptyList(),
    val selectedCurrencyId: String = AppCurrency.IDR.code,
    val initialSelectedCurrencyId: String = AppCurrency.IDR.code,
    /** Gagal memuat daftar mata uang; null = tidak ada error. */
    val error: UiText? = null,
) {
    val hasChange: Boolean = selectedCurrencyId != initialSelectedCurrencyId
}
