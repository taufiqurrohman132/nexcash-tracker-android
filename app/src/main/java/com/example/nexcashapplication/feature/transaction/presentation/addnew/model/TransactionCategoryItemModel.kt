package com.example.nexcashapplication.feature.transaction.presentation.addnew.model

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.core.domain.model.CategoryIcon
import com.example.nexcashapplication.core.domain.model.TypeCategory

// ============ MODEL ============
data class TransactionCategoryItemModel(
    val id: String,
    val name: String,
    val tipe: TypeCategory,
    val icon: CategoryIcon,
    val iconColor: Color,
)

