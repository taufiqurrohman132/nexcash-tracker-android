package com.example.nexcashapplication.feature.common.extentions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt

fun Color.toHex(): String =
    String.format("#%08X", this.toArgb())

fun String.toColor(): Color =
    try {
        Color(this.toColorInt())
    } catch (e: IllegalArgumentException) {
        Color.Black // fallback kalau hex string-nya invalid/corrupt
    }

fun List<Color>.moveToFront(color: Color, maxSize: Int? = null): List<Color> {
    val combined = listOf(color) + this.filterNot { it == color }
    return if (maxSize != null) combined.take(maxSize) else combined
}

fun List<Color>.sortedColor(color: Color, limit: Int): List<Color> {
    val lastRecentColors = if (this.contains(color)) {
        this
    } else {
        (listOf(color) + this.filterNot { it == color }).take(limit)
    }
    return lastRecentColors
}