package com.example.nexcashapplication.feature.category.presentation.main

sealed interface CategoryEffect {
    data class NavigateToAddCategory(val categoryId: String? = null, val parentId: String? = null) : CategoryEffect
    data object NavigateBack : CategoryEffect
    data class NavigateToDetail(val categoryId: String) : CategoryEffect
}