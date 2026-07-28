package com.example.nexcashapplication.feature.category.presentation.main.model

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.core.domain.model.CategoryIcon
import com.example.nexcashapplication.core.domain.model.TypeCategory

// ============ MODEL ============
data class CategoryItem(
    val id: String,
    val nama: String,
    val tipe: TypeCategory,
    val icon: CategoryIcon,
    val iconColor: Color,
    val terpakai: Long,
    val budget: Long,
) {
    val persentase: Int
        get() = if (budget == 0L) 0 else ((terpakai * 100) / budget).toInt()


}

