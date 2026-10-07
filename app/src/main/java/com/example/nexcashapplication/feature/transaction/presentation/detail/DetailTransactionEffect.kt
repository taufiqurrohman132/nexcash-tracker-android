package com.example.nexcashapplication.feature.transaction.presentation.detail

import com.example.nexcashapplication.core.i18n.UiText

sealed interface DetailTransactionEffect {
    data class NavigateToEdit(val transactionId: String) : DetailTransactionEffect
    data object NavigateBack : DetailTransactionEffect

    /**
     * Teks yang dibagikan — per baris [UiText] supaya labelnya (Tanggal, Kategori,
     * Sumber Dana, Catatan) ikut bahasa aktif; resolve saat dibuat share sheet.
     */
    data class ShareTransaction(val lines: List<UiText>) : DetailTransactionEffect

    data class ShowSnackbar(val message: UiText) : DetailTransactionEffect
}
