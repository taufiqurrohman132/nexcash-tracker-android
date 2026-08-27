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
    data class OnSubCategoryClick(val subCategoryId: String? = null) : DetailCategoryEvent
    data class OnTransactionClick(val transactionId: String? = null) : DetailCategoryEvent
    data object OnLoadMoreTransactionsClick : DetailCategoryEvent
    data object OnFabAddClick : DetailCategoryEvent
    data object OnRetryClick : DetailCategoryEvent
    object OnEditBudgetClick : DetailCategoryEvent
    object OnSetNotificationClick : DetailCategoryEvent
    object OnAddNoteClick : DetailCategoryEvent
}