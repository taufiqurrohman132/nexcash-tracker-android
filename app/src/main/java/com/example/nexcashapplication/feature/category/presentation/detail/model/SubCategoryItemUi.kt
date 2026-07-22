package com.example.nexcashapplication.feature.category.presentation.detail.model

import androidx.compose.ui.graphics.vector.ImageVector

data class SubCategoryItemUi(
    val id: Long,
    val name: String,
    val icon: ImageVector,
    val amount: String,
    val percentage: Int
)