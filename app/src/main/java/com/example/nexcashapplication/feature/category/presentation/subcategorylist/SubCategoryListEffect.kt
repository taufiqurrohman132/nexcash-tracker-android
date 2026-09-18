package com.example.nexcashapplication.feature.category.presentation.subcategorylist

// SubCategoryListEffect.kt
sealed interface SubCategoryListEffect {
    data object NavigateBack : SubCategoryListEffect
    data class NavigateToSubCategoryDetail(val subCategoryId: String) : SubCategoryListEffect
}