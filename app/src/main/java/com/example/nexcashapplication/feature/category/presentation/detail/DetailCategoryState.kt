package com.example.nexcashapplication.feature.category.presentation.detail

import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel
import com.example.nexcashapplication.feature.category.presentation.detail.model.CategoryUiModel


data class DetailCategoryState(
    val isLoading: Boolean = false,
    val category: CategoryUiModel? = null,
    val subCategories: List<CategoryUiModel> = emptyList(),
    val weeklyData: List<Float> = listOf(300f, 350f, 280f, 450f, 550f, 850f, 650f),
    val weeklyLabels: List<String> = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"),
    val recentTransactions: List<TransactionItemUiModel> = emptyList(),
    val isTransactionsLoadingMore: Boolean = false,
    val canLoadMoreTransactions: Boolean = true,
    val error: String? = null,
)