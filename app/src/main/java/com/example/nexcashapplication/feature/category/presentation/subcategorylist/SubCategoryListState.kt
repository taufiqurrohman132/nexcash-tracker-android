package com.example.nexcashapplication.feature.category.presentation.subcategorylist

import com.example.nexcashapplication.feature.category.presentation.shared.model.CategoryUiItemModel

// SubCategoryListState.kt
data class SubCategoryListState(
    val isLoading: Boolean = false,
    val parentCategoryName: String = "",
    val subCategories: List<CategoryUiItemModel> = emptyList(),
    val error: String? = null,
)