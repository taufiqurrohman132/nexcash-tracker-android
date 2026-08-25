package com.example.nexcashapplication.feature.account.presentation.addedit.typeoption

import com.example.nexcashapplication.feature.account.domain.model.TypeAccount

sealed interface AddEditTypeAccountEvent {
    data object OnNextClick : AddEditTypeAccountEvent
    data object OnBackClick : AddEditTypeAccountEvent
    data class OnTypeAccountSelected(val typeAccount: TypeAccount) : AddEditTypeAccountEvent
}