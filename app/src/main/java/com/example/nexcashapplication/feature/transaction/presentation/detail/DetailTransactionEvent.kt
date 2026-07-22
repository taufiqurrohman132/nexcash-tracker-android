package com.example.nexcashapplication.feature.transaction.presentation.detail

sealed interface DetailTransactionEvent {
    // Search
    data class OnSearchQueryChange(val query: String) : DetailTransactionEvent
    data object OnSearchClear : DetailTransactionEvent

    // Filter kategori
    data class OnCategorySelected(val category: String) : DetailTransactionEvent

    // Group transaksi (expand/collapse chevron)
    data class OnGroupToggle(val groupLabel: String) : DetailTransactionEvent

    // Item transaksi diklik (ke detail)
    data class OnTransactionClick(val transactionId: String) : DetailTransactionEvent

    // Floating
    data object OnAddTransactionClick : DetailTransactionEvent

    // Refresh (pull to refresh)
    data object OnRefresh : DetailTransactionEvent
}