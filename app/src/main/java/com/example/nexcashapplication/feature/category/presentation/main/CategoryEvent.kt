package com.example.nexcashapplication.feature.category.presentation.main

import com.example.nexcashapplication.core.domain.model.TypeCategory


// ui/kategori/KategoriIntent.kt
sealed interface CategoryEvent {
    data class OnSearchQueryChange(val query: String) : CategoryEvent

    //    data class OnFilterSelect(val filter: CategoryFilter) : CategoryEvent
    data class OnKategoriClick(val id: String) : CategoryEvent
    data object OnAddKategoriClick : CategoryEvent

    // tab category selected
    data class OnTabCategoryClick(val type: TypeCategory) : CategoryEvent
    data class OnPageChanged(val index: Int) : CategoryEvent

}