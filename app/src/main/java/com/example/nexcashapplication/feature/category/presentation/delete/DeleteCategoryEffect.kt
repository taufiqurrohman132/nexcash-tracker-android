package com.example.nexcashapplication.feature.category.presentation.delete

import com.example.nexcashapplication.core.i18n.UiText

sealed interface DeleteCategoryEffect {
    data object NavigateBack : DeleteCategoryEffect
    data class ShowError(val message: UiText) : DeleteCategoryEffect
    data object DeleteSuccess : DeleteCategoryEffect
}
