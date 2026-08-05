package com.example.nexcashapplication.feature.category.domain.model

sealed interface CategoryDeleteResolution {
    data object DeleteAllTransactions : CategoryDeleteResolution
    data class MoveToCategory(val targetCategoryId: String) : CategoryDeleteResolution
    data object Uncategorize : CategoryDeleteResolution
}
