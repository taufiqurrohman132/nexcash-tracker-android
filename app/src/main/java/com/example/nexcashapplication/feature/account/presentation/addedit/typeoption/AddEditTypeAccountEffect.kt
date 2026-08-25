package com.example.nexcashapplication.feature.account.presentation.addedit.typeoption

import com.example.nexcashapplication.feature.account.domain.model.TypeAccount

sealed interface AddEditTypeAccountEffect {
    data class NavigateToAddEditInfoAccount(val typeAccount: TypeAccount) : AddEditTypeAccountEffect
    data object NavigateBack : AddEditTypeAccountEffect
    data class ShowError(val message: String) : AddEditTypeAccountEffect
}