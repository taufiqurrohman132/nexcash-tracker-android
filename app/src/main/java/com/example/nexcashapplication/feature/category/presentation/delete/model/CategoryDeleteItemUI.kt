package com.example.nexcashapplication.feature.category.presentation.delete.model

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.core.domain.model.CategoryIcon

data class CategoryDeleteItemUI(
    val id: String,
    val name: String,
    val icon: CategoryIcon,
    val iconColor: Color,
)
