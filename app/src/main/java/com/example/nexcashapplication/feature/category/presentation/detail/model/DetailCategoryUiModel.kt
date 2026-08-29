package com.example.nexcashapplication.feature.category.presentation.detail.model

import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel

data class DetailCategoryUiModel(
    val category: CategoryUiModel?,
    val subCategories: List<CategoryUiModel> = emptyList(),
    val weeklyData: List<Float> = emptyList(),
    val weeklyLabels: List<String> = emptyList(),
    val recentTransactions: List<TransactionItemUiModel> = emptyList(),

)