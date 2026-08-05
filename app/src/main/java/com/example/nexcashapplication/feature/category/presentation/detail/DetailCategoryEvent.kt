package com.example.nexcashapplication.feature.category.presentation.detail

sealed interface DetailCategoryEvent {
    data object OnBackClick : DetailCategoryEvent
    data object OnMenuClick : DetailCategoryEvent          // ikon "..."
    data object OnEditClick : DetailCategoryEvent
    data object OnDeleteClick : DetailCategoryEvent
    data object OnAddTransactionClick : DetailCategoryEvent
    data object OnAddSubCategoryClick : DetailCategoryEvent
    data object OnManageSubCategoryClick : DetailCategoryEvent
    data object OnReminderClick : DetailCategoryEvent
    data object OnSeeAllSubCategoryClick : DetailCategoryEvent
    data class OnSubCategoryClick(val subCategoryId: String) : DetailCategoryEvent
    data class OnTransactionClick(val transactionId: String) : DetailCategoryEvent
    data object OnLoadMoreTransactionsClick : DetailCategoryEvent
    data object OnFabAddClick : DetailCategoryEvent
    data object OnRetryClick : DetailCategoryEvent
}