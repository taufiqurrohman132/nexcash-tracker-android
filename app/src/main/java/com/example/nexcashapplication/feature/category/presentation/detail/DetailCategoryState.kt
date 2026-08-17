package com.example.nexcashapplication.feature.category.presentation.detail

import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel
import com.example.nexcashapplication.feature.category.presentation.detail.model.CategoryDetail


data class DetailCategoryState(
    val isLoading: Boolean = false,
    val category: CategoryDetail? = null,
    val subCategories: List<CategoryDetail> = emptyList(),
    val recentTransactions: List<TransactionItemUiModel> = emptyList(),
    val isTransactionsLoadingMore: Boolean = false,
    val canLoadMoreTransactions: Boolean = true,
    val error: String? = null,
)