package com.example.nexcashapplication.feature.common.extentions

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.core.domain.model.CategoryIcon


fun List<CategoryIcon>.moveToFront(icon: CategoryIcon, maxSize: Int? = null): List<CategoryIcon> {
    val combined = listOf(icon) + this.filterNot { it == icon }
    return if (maxSize != null) combined.take(maxSize) else combined
}

fun List<CategoryIcon>.sortedIcon(icon: CategoryIcon, limit: Int): List<CategoryIcon> {
    val lastRecentColors = if (this.contains(icon)) {
        this
    } else {
        (listOf(icon) + this.filterNot { it == icon }).take(limit)
    }
    return lastRecentColors
}