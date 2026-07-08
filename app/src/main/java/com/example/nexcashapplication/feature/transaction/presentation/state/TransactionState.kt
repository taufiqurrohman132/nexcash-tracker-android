package com.example.nexcashapplication.feature.transaction.presentation.state

import com.example.nexcashapplication.core.datasource.local.model.CategoryEntity
import com.example.nexcashapplication.core.domain.model.TransactionUi

data class TransactionState(
    val searchQuery: String = "",
    val caategoryList: List<String> = emptyList(),
    val selectedCategory: String = "Semua",

    // summary card
    val todayTotal: Long = 0L,
    val weekTotal: Long = 0L,
    val remainingBudget: Long = 0L,

    val groupedTransactions: Map<String, List<TransactionUi>> = emptyMap(),
    val expandedGroups: Set<String> = emptySet(), // group yang sedang expand

    // Loading & error
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)


