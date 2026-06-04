package com.example.nexcashapplication.feature.dashboard.domain.model

data class MonthlySummary(
    val totalPemasukan: Long,
    val totalPengeluaran: Long,
    val totalSaldo: Long,
    val bulan: Int,
    val tahun: Int
)
