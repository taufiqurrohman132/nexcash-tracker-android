package com.example.nexcashapplication.feature.transaction.presentation.main

import java.time.YearMonth

sealed interface TransactionEvent {

    // Navigasi bulan di TopAppBar
    data class OnMonthSelected(val month: YearMonth) : TransactionEvent
    data object OnOpenMonthPicker : TransactionEvent
    data object OnMonthPickerDismiss : TransactionEvent

    // Search
    data class OnSearchQueryChange(val query: String) : TransactionEvent
    data object OnSearchClear : TransactionEvent

    // Filter kategori
    data class OnCategorySelected(val categoryId: String?) : TransactionEvent

    // Group transaksi (expand/collapse chevron)
    data class OnGroupToggle(val groupLabel: String) : TransactionEvent

    // Item transaksi diklik (ke detail)
    data class OnTransactionClick(val transactionId: String?) : TransactionEvent

    // Floating
    data object OnAddTransactionClick : TransactionEvent

    // Refresh (pull to refresh)
    data object OnRefresh : TransactionEvent
}