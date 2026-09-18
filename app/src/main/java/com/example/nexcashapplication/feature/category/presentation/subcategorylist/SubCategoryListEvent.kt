package com.example.nexcashapplication.feature.category.presentation.subcategorylist

// SubCategoryListEvent.kt
sealed interface SubCategoryListEvent {
    data object OnBackClick : SubCategoryListEvent
    data class OnSubCategoryClick(val subCategoryId: String) : SubCategoryListEvent
    data object OnRetryClick : SubCategoryListEvent
}
