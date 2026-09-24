package com.example.nexcashapplication.feature.category.presentation.detail

sealed interface DetailCategoryEvent {
    data object OnBackClick : DetailCategoryEvent

    data object OnEditClick : DetailCategoryEvent
    data object OnDeleteClick : DetailCategoryEvent
    data object OnAddSubCategoryClick : DetailCategoryEvent
    data object OnSeeAllSubCategoryClick : DetailCategoryEvent
    data class OnSubCategoryClick(val subCategoryId: String? = null) : DetailCategoryEvent
    data class OnTransactionClick(val transactionId: String? = null) : DetailCategoryEvent
    data object OnLoadMoreTransactionsClick : DetailCategoryEvent
    object OnEditBudgetClick : DetailCategoryEvent
    object OnSetNotificationClick : DetailCategoryEvent
    object OnAddNoteClick : DetailCategoryEvent
}