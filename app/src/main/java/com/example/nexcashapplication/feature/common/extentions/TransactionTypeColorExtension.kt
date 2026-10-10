package com.example.nexcashapplication.feature.common.extentions

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.core.domain.model.TypeCategory

@Composable
fun TypeCategory.toAmountColor(): Color = when (this) {
    TypeCategory.INCOME -> Color.Green
    TypeCategory.EXPENSE -> MaterialTheme.colorScheme.error
}