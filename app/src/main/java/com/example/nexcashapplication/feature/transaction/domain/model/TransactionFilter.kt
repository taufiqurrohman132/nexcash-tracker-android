package com.example.nexcashapplication.feature.transaction.domain.model

import kotlinx.serialization.Serializable

/**
 * Filter halaman "Semua Transaksi" ([com.example.nexcashapplication.core.navigations.Route.TransactionRoute.All]).
 *
 * Semua titik "Lihat Semua" (detail kategori, detail akun, dst.) berbagi **satu layar +
 * satu ViewModel**; yang membedakan hanya filter ini yang dibawa lewat route.
 * Menambah varian filter baru = menambah varian sealed di sini + satu cabang di
 * `GetTransactionsByFilterUseCase` — layar dan ViewModel tidak berubah (ADR-002).
 */
@Serializable
sealed interface TransactionFilter {

    /** Tanpa filter — seluruh transaksi. */
    @Serializable
    data object None : TransactionFilter

    /** Transaksi kategori [categoryId] **termasuk** seluruh subkategori turunannya. */
    @Serializable
    data class ByCategory(val categoryId: String) : TransactionFilter

    /** Transaksi milik akun [accountId]. */
    @Serializable
    data class ByAccount(val accountId: String) : TransactionFilter
}
