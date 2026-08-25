package com.example.nexcashapplication.feature.category.presentation.detail.model

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.core.domain.model.CategoryIcon
import com.example.nexcashapplication.core.domain.model.TypeCategory

data class CategoryUiModel(
    val id: String,
    val parentId: String? = null,
    val name: String,
    val icon: CategoryIcon,
    val color: Color,
    val totalBudget: Long,
    val usedBudget: Long,
    val typeCategory: TypeCategory,

    ) {
    val remainingBudget: Long = totalBudget - usedBudget
    val usedPercentage: Int = if (totalBudget > 0) {
        ((usedBudget.toFloat() / totalBudget) * 100).toInt()
    } else 0
}
