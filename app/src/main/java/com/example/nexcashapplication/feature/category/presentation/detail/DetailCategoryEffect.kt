package com.example.nexcashapplication.feature.category.presentation.detail

import com.example.nexcashapplication.core.domain.model.TypeCategory

sealed interface DetailCategoryEffect {
    data object NavigateBack : DetailCategoryEffect
    data class NavigateToEditCategory(val categoryId: String) : DetailCategoryEffect
    data class NavigateToEditBudget(val budgetId: String? = null, val categoryId: String? = null) :
        DetailCategoryEffect

    data class NavigateToDeleteCategory(val categoryId: String, val typeCategory: TypeCategory) :
        DetailCategoryEffect

    data class NavigateToAddTransaction(val categoryId: String) : DetailCategoryEffect
    data class NavigateToAddSubCategory(val parentId: String) : DetailCategoryEffect
    data class NavigateToSubCategoryManagement(val categoryId: String) : DetailCategoryEffect
    data class NavigateToSubCategoryDetail(val subCategoryId: String) : DetailCategoryEffect
    data class NavigateToTransactionDetail(val transactionId: String) : DetailCategoryEffect
    data class NavigateToAllSubCategories(val categoryId: String) : DetailCategoryEffect
    data class ShowSnackbar(val message: String) : DetailCategoryEffect
    data object ShowMenuBottomSheet : DetailCategoryEffect
}