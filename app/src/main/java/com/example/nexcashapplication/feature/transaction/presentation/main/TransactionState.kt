package com.example.nexcashapplication.feature.transaction.presentation.main

import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel
import com.example.nexcashapplication.feature.transaction.presentation.main.model.TransactionUiGroupModel

data class TransactionState(
    val searchQuery: String = "",
    val caategoryList: List<String> = emptyList(),
    val selectedCategory: String = "Semua",

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