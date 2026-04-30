package com.example.nexcashapplication.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

object AppShapes {
    // Card utama / surface
    val card = RoundedCornerShape(12.dp)

    // Button CTA
    val button = RoundedCornerShape(12.dp)

    // Input field
    val input = RoundedCornerShape(8.dp)

    // Badge / chip / tag
    val pill = RoundedCornerShape(99.dp)

    // Ikon kategori / FAB bulat
    val circle = CircleShape

    // Bottom sheet (atas saja)
    val bottomSheet = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp
    )

    // Progress bar rounded
    val progress = RoundedCornerShape(99.dp)

    // Header saldo cyan
    val headerCard = RoundedCornerShape(16.dp)
}