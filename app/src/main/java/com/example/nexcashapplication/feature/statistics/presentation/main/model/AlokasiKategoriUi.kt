package com.example.nexcashapplication.feature.statistics.presentation.main.model

data class AlokasiKategoriUi(
    val kategoriId: String,
    val nama: String,
    val persentase: Float,
    val nominal: Long,
    val colorHex: String, // domain tetap String, no Compose dependency
)