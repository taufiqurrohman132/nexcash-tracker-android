package com.example.nexcashapplication.feature.transaction.presentation.addnew


//apa yang terjadi SEKALI, bukan bagian dari state"
sealed interface AddEditTransactionEffect {
    data object NavigateBack : AddEditTransactionEffect
    data class ShowSnackbar(val message: String) : AddEditTransactionEffect
}