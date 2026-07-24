package com.example.nexcashapplication.feature.category.presentation.detail

sealed interface DetailCategoryEvent {
    data object onAddTransactionClick : DetailCategoryEvent
    data class onSubcategoryClick(val id: Long) : DetailCategoryEvent
    data object onReminderClick : DetailCategoryEvent
    data object onSeeAllSubcategoriesClick : DetailCategoryEvent
    data object onLoadMoreTransactions : DetailCategoryEvent
}