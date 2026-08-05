package com.example.nexcashapplication.feature.category.presentation.delete

import com.example.nexcashapplication.feature.category.presentation.delete.model.CategoryDeleteItemUI
import com.example.nexcashapplication.feature.category.presentation.delete.model.DeleteOption

sealed interface DeleteCategoryEvent {
    data class SelectOption(val option: DeleteOption) : DeleteCategoryEvent
    data class SelectTargetCategory(val category: CategoryDeleteItemUI) : DeleteCategoryEvent
    data object ToggleDropdown : DeleteCategoryEvent
    data object ConfirmDelete : DeleteCategoryEvent
    data object Cancel : DeleteCategoryEvent
}