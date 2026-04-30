package com.example.nexcashapplication.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with

// Gunakan FontFamily.Default
// Jika nanti pakai Inter / Plus Jakarta Sans tinggal ganti di sini
val AppFontFamily = FontFamily.Default

val Typography = Typography(

    // ===============================
    // Page Title (Header)
    // 20sp / SemiBold
    // ===============================
    headlineMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),

    // ===============================
    // Section Title
    // 16sp / SemiBold
    // ===============================
    titleLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),

    // ===============================
    // Body Primary
    // 14sp / Normal
    // ===============================
    bodyLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),

    // ===============================
    // Body Secondary
    // 14sp / Normal
    // ===============================
    bodyMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),

    // ===============================
    // Caption / Hint
    // 12sp / Normal
    // ===============================
    bodySmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),

    // ===============================
    // Amount Besar / Hero
    // 32sp / Bold
    // ===============================
    displaySmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),

    // ===============================
    // Amount Kecil
    // 16sp / SemiBold
    // ===============================
    titleMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),

    // ===============================
    // Bottom Nav Label
    // 10sp / Normal
    // ===============================
    labelSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 12.sp
    )
)