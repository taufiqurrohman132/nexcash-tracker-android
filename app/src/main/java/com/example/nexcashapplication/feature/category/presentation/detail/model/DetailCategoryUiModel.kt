package com.example.nexcashapplication.feature.category.presentation.detail.model

import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel
import com.example.nexcashapplication.feature.category.presentation.shared.model.CategoryUiItemModel

data class DetailCategoryUiModel(
    val category: CategoryUiItemModel?,
    val subCategories: List<CategoryUiItemModel> = emptyList(),
    val weeklyData: List<Float> = emptyList(),
    val weeklyLabels: List<String> = emptyList(),
    val recentTransactions: List<TransactionItemUiModel> = emptyList(),
)