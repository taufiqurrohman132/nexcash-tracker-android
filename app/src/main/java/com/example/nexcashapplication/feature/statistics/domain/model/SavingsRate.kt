package com.example.nexcashapplication.feature.statistics.domain.model

/**
 * Tingkat tabungan satu bulan: persentase pemasukan yang tidak terpakai,
 * beserta tren terhadap bulan lalu (selisih poin persen; 0 bila bulan lalu
 * tidak ada pemasukan untuk dibandingkan).
 */
data class SavingsRate(
    val rate: Float,
    val trendPercent: Float,
)
