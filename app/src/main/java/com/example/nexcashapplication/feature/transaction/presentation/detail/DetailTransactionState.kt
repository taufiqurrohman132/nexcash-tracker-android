package com.example.nexcashapplication.feature.transaction.presentation.detail

import com.example.nexcashapplication.feature.transaction.presentation.detail.model.DetailTransactionUiModel
import com.example.nexcashapplication.feature.transaction.presentation.detail.state.DeleteTransactionDialogUiState

data class DetailTransactionState(
    val isLoading: Boolean = true,
    val transaction: DetailTransactionUiModel? = null,
    val isDeleting: Boolean = false,
    val deleteDialogState: DeleteTransactionDialogUiState? = null,
    val errorMessage: String? = null,
)