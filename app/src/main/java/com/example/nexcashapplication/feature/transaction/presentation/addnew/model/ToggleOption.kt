package com.example.nexcashapplication.feature.transaction.presentation.addnew.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector


/**
 * Model data opsi pilihan untuk [SegmentedTypeToggle].
 */
data class ToggleOption<T>(
    val value: T,
    val label: String,
    val icon: ImageVector? = null,
    val selectedColor: Color = Color(0xFF003DFF),
)
