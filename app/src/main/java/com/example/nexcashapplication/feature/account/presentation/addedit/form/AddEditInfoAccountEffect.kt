package com.example.nexcashapplication.feature.account.presentation.addedit.form

import com.example.nexcashapplication.core.i18n.UiText

sealed interface AddEditInfoAccountEffect {
    data object NavigateBack : AddEditInfoAccountEffect
    data object NavigateToProviderList : AddEditInfoAccountEffect
    data object AssetSavedNavigateMain : AddEditInfoAccountEffect
    data class ShowError(val message: UiText) : AddEditInfoAccountEffect
}
