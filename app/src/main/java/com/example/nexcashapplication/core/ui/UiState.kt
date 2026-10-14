package com.example.nexcashapplication.core.ui

import com.example.nexcashapplication.core.i18n.UiText

sealed class UiState<out T> {

    data object Loading : UiState<Nothing>()

    data class Success<out T : Any>(val data: T) : UiState<T>()

    /**
     * Gagal memuat data. Membawa [UiText] (bukan [String] mentah) supaya pesannya
     * bisa diterjemahkan saat ditampilkan — lihat [com.example.nexcashapplication.core.i18n.UiText].
     */
    data class Error(val message: UiText) : UiState<Nothing>()
}

val <T> UiState<T>.dataOrNull: T?
    get() = (this as? UiState.Success)?.data