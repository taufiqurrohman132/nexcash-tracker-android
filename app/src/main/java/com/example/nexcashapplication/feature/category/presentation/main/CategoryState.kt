package com.example.nexcashapplication.feature.category.presentation.main

import com.example.nexcashapplication.feature.category.presentation.main.model.CategoryFilter
import com.example.nexcashapplication.feature.category.presentation.main.model.CategoryItem

// ui/kategori/KategoriState.kt
data class CategoryState(
    val isLoading: Boolean = false,
    val categoryList: List<CategoryItem> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: CategoryFilter = CategoryFilter.EXPENSE,
    val totalAnggaran: Long = 0L,
    val sisaAnggaran: Long = 0L,
    val error: String? = null
) {
    val filteredCategoryList: List<CategoryItem>
        get() = categoryList.filter { item ->
            val matchesSearch = item.nama.contains(searchQuery, ignoreCase = true)
            val matchesFilter = item.tipe == selectedFilter
            matchesSearch && matchesFilter
        }
}
