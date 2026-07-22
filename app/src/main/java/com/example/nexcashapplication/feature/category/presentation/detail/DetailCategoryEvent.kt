package com.example.nexcashapplication.feature.category.presentation.detail

import com.example.nexcashapplication.feature.category.presentation.main.CategoryEvent
import com.example.nexcashapplication.feature.category.presentation.main.model.CategoryFilter

sealed interface DetailCategoryEvent {
    data object onAddTransactionClick : DetailCategoryEvent
    data class onSubcategoryClick(val id: Long) : DetailCategoryEvent
    data object onReminderClick : DetailCategoryEvent
    data object onSeeAllSubcategoriesClick : DetailCategoryEvent
    data object onLoadMoreTransactions : DetailCategoryEvent
}