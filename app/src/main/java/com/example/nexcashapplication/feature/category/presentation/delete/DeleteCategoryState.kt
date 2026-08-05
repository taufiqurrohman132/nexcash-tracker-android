package com.example.nexcashapplication.feature.category.presentation.delete

import com.example.nexcashapplication.feature.category.presentation.delete.model.CategoryDeleteItemUI
import com.example.nexcashapplication.feature.category.presentation.delete.model.DeleteOption

data class DeleteCategoryState(
    val categoryId: String = "",
    val categoryName: String = "",
    val transactionCount: Int = 0,
    val selectedOption: DeleteOption = DeleteOption.MOVE_TO_CATEGORY,
    val availableCategories: List<CategoryDeleteItemUI> = emptyList(),
    val targetCategory: CategoryDeleteItemUI? = null, // null = user belum pilih
    val isDropdownExpanded: Boolean = false,
    val isSubmitting: Boolean = false,
    val isLoading: Boolean = true,
) {
    val isConfirmEnabled: Boolean
        get() = when (selectedOption) {
            DeleteOption.MOVE_TO_CATEGORY -> targetCategory != null
            DeleteOption.DELETE_ALL_TRANSACTIONS, DeleteOption.UNCATEGORIZED -> true
        } && !isSubmitting
}