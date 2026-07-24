package com.example.nexcashapplication.feature.category.presentation.main

import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.category.presentation.main.model.CategoryItem

// ui/kategori/KategoriState.kt
data class CategoryState(
    val isLoading: Boolean = false,
    val categoryList: List<CategoryItem> = emptyList(),
    val searchQuery: String = "",
    val totalAnggaran: Long = 0L,
    val sisaAnggaran: Long = 0L,
    val selectedTabCategory: TypeCategory = TypeCategory.EXPANSE,
    val error: String? = null,
) {
//    val filteredCategoryList: List<CategoryItem>
//        get() = categoryList.filter { item ->
//            val matchesSearch = item.nama.contains(searchQuery, ignoreCase = true)
//            val matchesType = item.tipe == selectedTabCategory
//            matchesSearch && matchesType
//        }
}
