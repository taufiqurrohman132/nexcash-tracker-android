package com.example.nexcashapplication.feature.transaction.presentation.alltransactions

import com.example.nexcashapplication.feature.transaction.presentation.main.model.TransactionUiGroupModel

/**
 * State halaman Semua Transaksi.
 * Sengaja generik — tidak ada field spesifik per filter; grouping per tanggal
 * mengikuti tab Transaksi utama (ADR-002).
 */
data class AllTransactionsState(
    val isLoading: Boolean = true,
    val title: String = "Semua Transaksi",
    val groupedTransactions: List<TransactionUiGroupModel> = emptyList(),

    /** Id header grup (tanggal) yang sedang dilipat — sama seperti tab Transaksi. */
    val collapsedGroups: Set<String> = emptySet(),
    val error: String? = null,
)
