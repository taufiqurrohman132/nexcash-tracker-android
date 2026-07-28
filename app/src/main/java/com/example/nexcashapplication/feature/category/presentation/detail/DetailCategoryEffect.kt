package com.example.nexcashapplication.feature.category.presentation.detail

sealed interface DetailCategoryEffect {
    data object NavigateBack : DetailCategoryEffect
    data class NavigateToEditCategory(val categoryId: String) : DetailCategoryEffect
    data class NavigateToAddTransaction(val categoryId: String) : DetailCategoryEffect
    data class NavigateToAddSubCategory(val categoryId: String) : DetailCategoryEffect
    data class NavigateToSubCategoryManagement(val categoryId: String) : DetailCategoryEffect
    data class NavigateToSubCategoryDetail(val subCategoryId: String) : DetailCategoryEffect
    data class NavigateToTransactionDetail(val transactionId: String) : DetailCategoryEffect
    data class NavigateToAllSubCategories(val categoryId: String) : DetailCategoryEffect
    data class ShowSnackbar(val message: String) : DetailCategoryEffect
    data object ShowMenuBottomSheet : DetailCategoryEffect
}