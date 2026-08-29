package com.example.nexcashapplication.feature.category.presentation.detail

import com.example.nexcashapplication.core.ui.UiState
import com.example.nexcashapplication.feature.category.presentation.detail.model.DetailCategoryUiModel

data class DetailCategoryState(
    val dataState: UiState<DetailCategoryUiModel> = UiState.Loading,
    val isTransactionsLoadingMore: Boolean = false,
    val canLoadMoreTransactions: Boolean = true,
)