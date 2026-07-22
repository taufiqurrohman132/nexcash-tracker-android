package com.example.nexcashapplication.feature.category.presentation.main

import com.example.nexcashapplication.feature.category.presentation.main.model.CategoryFilter

// ui/kategori/KategoriIntent.kt
sealed interface CategoryEvent {
    data class OnSearchQueryChange(val query: String) : CategoryEvent
    data class OnFilterSelect(val filter: CategoryFilter) : CategoryEvent
    data class OnKategoriClick(val id: Long) : CategoryEvent
    data object OnAddKategoriClick : CategoryEvent
    data object OnRefresh : CategoryEvent
}