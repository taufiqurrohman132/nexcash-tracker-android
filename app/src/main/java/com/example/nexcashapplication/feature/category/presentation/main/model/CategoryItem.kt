package com.example.nexcashapplication.feature.category.presentation.main.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.nexcashapplication.core.domain.model.TypeCategory

// ============ MODEL ============
data class CategoryItem(
    val id: Long,
    val nama: String,
    val tipe: TypeCategory,
    val icon: ImageVector,
    val iconBgColor: Color,
    val terpakai: Long,
    val budget: Long
) {
    val persentase: Int
        get() = if (budget == 0L) 0 else ((terpakai * 100) / budget).toInt()
}

