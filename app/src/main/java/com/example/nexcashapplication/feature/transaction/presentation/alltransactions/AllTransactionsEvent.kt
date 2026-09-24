package com.example.nexcashapplication.feature.transaction.presentation.alltransactions

/** Event halaman Semua Transaksi — sama untuk semua titik "Lihat Semua" (ADR-002). */
sealed interface AllTransactionsEvent {
    data object OnBackClick : AllTransactionsEvent
    data class OnTransactionClick(val transactionId: String) : AllTransactionsEvent
    data object OnAddTransactionClick : AllTransactionsEvent

    /** Lipat/buka grup tanggal berdasarkan [groupTitle] (`header.id`). */
    data class OnGroupToggle(val groupTitle: String) : AllTransactionsEvent
    data object OnRetryClick : AllTransactionsEvent
}
