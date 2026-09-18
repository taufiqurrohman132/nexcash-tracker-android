package com.example.nexcashapplication.feature.category.presentation.shared.model

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.core.domain.model.CategoryIcon
import com.example.nexcashapplication.core.domain.model.TypeCategory

data class CategoryUiItemModel(
    val id: String,
    val parentId: String? = null,
    val name: String,
    val icon: CategoryIcon,
    val color: Color,
    val totalBudget: Long = 0L,
    val usedBudget: Long = 0L,
    val typeCategory: TypeCategory,
) {
    val remainingBudget: Long get() = totalBudget - usedBudget
    val usedPercentage: Int
        get() = if (totalBudget > 0) {
            ((usedBudget.toFloat() / totalBudget) * 100).toInt()
        } else 0
}