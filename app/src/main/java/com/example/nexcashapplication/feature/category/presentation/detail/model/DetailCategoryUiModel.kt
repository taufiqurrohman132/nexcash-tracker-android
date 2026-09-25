package com.example.nexcashapplication.feature.category.presentation.detail.model

import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel
import com.example.nexcashapplication.feature.category.presentation.shared.model.CategoryUiItemModel

/**
 * Ringkasan anggaran **induk** untuk layar detail sub kategori (pola: sub ngikut induk).
 *
 * - [totalBudget] / [usedBudget]: budget induk + rollup seluruh keluarga.
 * - [thisUsedBudget]: terpakai sub ini pada periode induk → dasar hitung kontribusi.
 */
data class ParentBudgetUiModel(
    val id: String,
    val name: String,
    val totalBudget: Long,
    val usedBudget: Long,
    val thisUsedBudget: Long,
)

data class DetailCategoryUiModel(
    val category: CategoryUiItemModel?,
    val subCategories: List<CategoryUiItemModel> = emptyList(),
    val weeklyData: List<Float> = emptyList(),
    val weeklyLabels: List<String> = emptyList(),
    val recentTransactions: List<TransactionItemUiModel> = emptyList(),
    /** Null bila kategori bukan sub, belum termuat, atau induk gagal dimuat. */
    val parentInfo: ParentBudgetUiModel? = null,
)
