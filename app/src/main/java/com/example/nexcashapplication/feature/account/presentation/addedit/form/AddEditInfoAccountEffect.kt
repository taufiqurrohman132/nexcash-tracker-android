package com.example.nexcashapplication.feature.account.presentation.addedit.form

sealed interface AddEditInfoAccountEffect {
    data object NavigateBack : AddEditInfoAccountEffect
    data object NavigateToProviderList : AddEditInfoAccountEffect
    data object AssetSavedNavigateMain : AddEditInfoAccountEffect
    data class ShowError(val message: String) : AddEditInfoAccountEffect
}