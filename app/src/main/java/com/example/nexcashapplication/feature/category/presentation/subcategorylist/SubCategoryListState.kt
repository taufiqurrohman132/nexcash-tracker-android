package com.example.nexcashapplication.feature.category.presentation.subcategorylist

import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.feature.category.presentation.shared.model.CategoryUiItemModel

// SubCategoryListState.kt
data class SubCategoryListState(
    val isLoading: Boolean = false,
    val parentCategoryName: String = "",
    /** Budget induk — dipakai kartu sub tanpa budget sendiri (kontribusi "% dari budget induk"). */
    val parentTotalBudget: Long = 0L,
    val subCategories: List<CategoryUiItemModel> = emptyList(),
    /** Gagal memuat subkategori; null = tidak ada error. */
    val error: UiText? = null,
)
