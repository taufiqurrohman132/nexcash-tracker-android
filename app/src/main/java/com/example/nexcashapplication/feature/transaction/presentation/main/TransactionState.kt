package com.example.nexcashapplication.feature.transaction.presentation.main

import com.example.nexcashapplication.feature.transaction.presentation.main.model.TransactionUiGroupModel

data class TransactionState(
    val searchQuery: String = "",
    val caategoryList: List<String> = emptyList(),
    val selectedCategory: String = "Semua",

    // summary card
    val todayTotal: Long = 0L,
    val weekTotal: Long = 0L,
    val remainingBudget: Long = 0L,

    val groupedTransactions: List<TransactionUiGroupModel> = emptyList(),
    val expandedGroups: Set<String> = emptySet(), // group yang sedang expand

    // Loading & error
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)