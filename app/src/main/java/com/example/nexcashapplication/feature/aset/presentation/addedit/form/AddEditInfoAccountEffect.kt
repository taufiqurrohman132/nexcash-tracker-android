package com.example.nexcashapplication.feature.aset.presentation.addedit.form

sealed interface AddEditInfoAccountEffect {
    data object NavigateBack : AddEditInfoAccountEffect
    data object NavigateToProviderList : AddEditInfoAccountEffect
    data object AssetSavedNavigateHome : AddEditInfoAccountEffect
    data class ShowError(val message: String) : AddEditInfoAccountEffect
}