package com.example.nexcashapplication.feature.category.domain.model

import com.example.nexcashapplication.core.datasource.local.model.BudgetEntity
import com.example.nexcashapplication.core.datasource.local.model.CategoryEntity

// Untuk rincian subkategori di detail anggaran
data class SubCategoryExpense(
    val category: CategoryEntity,
    val budget: BudgetEntity?,
    val spent: Long
)