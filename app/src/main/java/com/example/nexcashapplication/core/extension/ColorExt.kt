package com.example.nexcashapplication.core.extension

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

fun List<Color>.moveToFront(color: Color, maxSize: Int = 8): List<Color> {
    val updated = this.toMutableList()
    updated.remove(color)           // hapus dulu kalau udah ada di list (biar gak duplikat)
    updated.add(0, color)           // taruh di paling depan
    return updated.take(maxSize)    // batasin jumlah maksimal, buang yang paling lama
}