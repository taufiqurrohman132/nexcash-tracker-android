package com.example.nexcashapplication.feature.category.presentation.main

import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.feature.category.presentation.main.model.CategoryItem

// ui/kategori/KategoriState.kt
data class CategoryState(
    val isLoading: Boolean = true,
    val expenseList: List<CategoryItem> = emptyList(),
    val incomeList: List<CategoryItem> = emptyList(),
    val categorySearchList: List<CategoryItem> = emptyList(),
    val searchQuery: String = "",
    val selectedTabCategory: TypeCategory = TypeCategory.EXPENSE,
    /** Gagal memuat daftar kategori; null = tidak ada error. */
    val error: UiText? = null,
) {

    private val activeList: List<CategoryItem>
        get() = when (selectedTabCategory) {
            TypeCategory.EXPENSE -> expenseList
            TypeCategory.INCOME -> incomeList
        }

    val totalAnggaran: Long
        get() = activeList.sumOf { it.budget }

    val sisaAnggaran: Long
        get() = activeList.sumOf { it.budget - it.terpakai }

    val totalPemasukan: Long get() = incomeList.sumOf { it.terpakai }
//    val filteredCategoryList: List<CategoryItem>
//        get() = categoryList.filter { item ->
//            val matchesSearch = item.nama.contains(searchQuery, ignoreCase = true)
//            val matchesType = item.tipe == selectedTabCategory
//            matchesSearch && matchesType
//        }
}
