package com.example.nexcashapplication.feature.category.presentation.transactionlist

import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel
import com.example.nexcashapplication.feature.category.presentation.shared.model.CategoryUiItemModel

// TransactionListState.kt
data class TransactionListState(
    val isLoading: Boolean = false,
    val categoryName: String = "",
    val transactions: List<TransactionItemUiModel> = emptyList(),
    val error: String? = null,
)
