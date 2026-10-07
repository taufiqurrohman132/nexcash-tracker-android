package com.example.nexcashapplication.feature.transaction.presentation.detail.state

import androidx.annotation.StringRes
import com.example.nexcashapplication.core.i18n.UiText

data class DeleteTransactionDialogUiState(
    val title: String,
    val formattedAmount: String,

    /** Kunci string resource label tipe transaksi — resolve dengan `stringResource(...)`. */
    @StringRes val typeLabelRes: Int,

    val accountName: String,

    /** Efek saldo ke akun — [UiText] karena memuat argumen, resolve saat ditampilkan. */
    val balanceEffectText: UiText, // udah berupa resource, bukan dihitung di Composable
)
