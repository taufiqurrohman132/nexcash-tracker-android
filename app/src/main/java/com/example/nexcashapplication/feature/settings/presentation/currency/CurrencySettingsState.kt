package com.example.nexcashapplication.feature.settings.presentation.currency

import com.example.nexcashapplication.core.domain.model.AppCurrency
import com.example.nexcashapplication.feature.settings.presentation.currency.model.CurrencyItemUiModel

data class CurrencySettingsState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val listCurrency: List<CurrencyItemUiModel> = emptyList(),
    val selectedCurrencyId: String = AppCurrency.IDR.code,
    val initialSelectedCurrencyId: String = AppCurrency.IDR.code,
    val error: String? = null,
) {
    val hasChange: Boolean = selectedCurrencyId != initialSelectedCurrencyId
}
