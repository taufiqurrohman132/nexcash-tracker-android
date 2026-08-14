package com.example.nexcashapplication.feature.aset.presentation.addedit.typeoption

import com.example.nexcashapplication.feature.aset.domain.model.TypeAccount

sealed interface AddEditTypeAccountEffect {
    data class NavigateToAddEditInfoAccount(val typeAccount: TypeAccount) : AddEditTypeAccountEffect
    data object NavigateBack : AddEditTypeAccountEffect
    data class ShowError(val message: String) : AddEditTypeAccountEffect
}