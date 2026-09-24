package com.example.nexcashapplication.feature.transaction.domain.model

import kotlinx.serialization.Serializable

/**
 * Filter halaman "Semua Transaksi" ([com.example.nexcashapplication.core.navigations.Route.TransactionRoute.All]).
 *
 * Semua titik "Lihat Semua" (detail kategori, detail akun, dst.) berbagi **satu layar +
 * satu ViewModel**; yang membedakan hanya filter ini. Route hanya membawa argumen
 * primitif (`categoryId`/`accountId`) karena inferensi NavType Navigation tidak
 * menutup tipe sealed — pemetaan ke varian di sini dilakukan lewat [fromRoute].
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

    companion object {
        /**
         * Petakan argumen primitif route `All(categoryId, accountId)` ke varian filter.
         *
         * - hanya [categoryId] terisi → [ByCategory]
         * - hanya [accountId] terisi → [ByAccount]
         * - keduanya terisi → [ByCategory] menang (guard; call site tidak pernah mengirim keduanya)
         * - keduanya `null` → [None]
         */
        fun fromRoute(categoryId: String?, accountId: String?): TransactionFilter = when {
            categoryId != null -> ByCategory(categoryId)
            accountId != null -> ByAccount(accountId)
            else -> None
        }
    }
}
