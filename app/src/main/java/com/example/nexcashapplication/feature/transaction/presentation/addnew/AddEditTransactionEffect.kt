package com.example.nexcashapplication.feature.transaction.presentation.addnew

import com.example.nexcashapplication.core.i18n.UiText


//apa yang terjadi SEKALI, bukan bagian dari state"
sealed interface AddEditTransactionEffect {
    data object NavigateBack : AddEditTransactionEffect
    data class ShowSnackbar(val message: UiText) : AddEditTransactionEffect
}