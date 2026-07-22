package com.example.nexcashapplication.feature.transaction.presentation.addnew


//apa yang terjadi SEKALI, bukan bagian dari state"
sealed interface AddTransactionEffect {
    data object NavigateBack : AddTransactionEffect
    data class ShowSnackbar(val message: String) : AddTransactionEffect
    data object NavigateToAddCategory : AddTransactionEffect
}