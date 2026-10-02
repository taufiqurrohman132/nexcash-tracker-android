package com.example.nexcashapplication.feature.transaction.presentation.main

import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel
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
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)