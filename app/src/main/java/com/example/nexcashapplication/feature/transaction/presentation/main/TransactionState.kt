package com.example.nexcashapplication.feature.transaction.presentation.main

import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.feature.transaction.presentation.main.model.CategoryNameItemUiModel
import com.example.nexcashapplication.feature.transaction.presentation.main.model.TransactionUiGroupModel
import java.time.YearMonth

data class TransactionState(
    val searchQuery: String = "",

    // Navigasi bulan di TopAppBar — filter daftar transaksi & ringkasan
    val selectedMonth: YearMonth = YearMonth.now(),
    val showMonthPicker: Boolean = false,

    val categoryList: List<CategoryNameItemUiModel?> = emptyList(),
    val selectedCategoryId: String? = null,

    // summary card
    val todayTotal: Long = 0L,
    val weekTotal: Long = 0L,
    val remainingBudget: Long = 0L,

    val trasactionsSearchList: List<TransactionItemUiModel> = emptyList(),
    val groupedTransactions: List<TransactionUiGroupModel> = emptyList(),
    val collapsGroups: Set<String> = emptySet(), // group yang sedang expand

    // Loading & error
    // true sampai emisi data pertama diterima — UI menampilkan indikator loading
    // menggantikan pesan "tidak ada transaksi"
    val isLoading: Boolean = true,
    /** Gagal memuat daftar transaksi; null = tidak ada error. */
    val errorMessage: UiText? = null,
)