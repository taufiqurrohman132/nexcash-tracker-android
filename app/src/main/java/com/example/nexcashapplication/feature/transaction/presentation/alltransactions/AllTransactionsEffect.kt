package com.example.nexcashapplication.feature.transaction.presentation.alltransactions

/** Efek halaman Semua Transaksi — sama untuk semua titik "Lihat Semua" (ADR-002). */
sealed interface AllTransactionsEffect {
    data object NavigateBack : AllTransactionsEffect
    data class NavigateToTransactionDetail(val transactionId: String) : AllTransactionsEffect
    data object NavigateToAddTransaction : AllTransactionsEffect
}
