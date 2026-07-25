package com.example.nexcashapplication.feature.category.presentation.navigation

sealed interface CategoryNavigationEvent {
    data object NavigateToAddCategory : CategoryNavigationEvent
    data object NavigateBack : CategoryNavigationEvent
    data class NavigateToDetail(val categoryId: Long) : CategoryNavigationEvent
}