package com.example.nexcashapplication.feature.settings.presentation.currency.model

import androidx.annotation.StringRes

data class CurrencyItemUiModel(
    val id: String,

    /** Nama Indonesia — kunci pencarian & unit test; tampilan memakai [nameRes]. */
    val name: String,

    val symbol: String,

    /** Nama tampil dari resource (`settings_currency_*`), di-resolve dengan `stringResource`. */
    @StringRes val nameRes: Int,
)
