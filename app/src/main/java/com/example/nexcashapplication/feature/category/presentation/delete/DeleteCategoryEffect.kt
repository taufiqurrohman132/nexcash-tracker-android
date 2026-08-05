package com.example.nexcashapplication.feature.category.presentation.delete

sealed interface DeleteCategoryEffect {
    data object NavigateBack : DeleteCategoryEffect
    data class ShowError(val message: String) : DeleteCategoryEffect
    data object DeleteSuccess : DeleteCategoryEffect
}