package com.example.nexcashapplication.feature.statistics.domain.model

/**
 * Kesehatan anggaran satu bulan: total budget, sisa (total − pengeluaran
 * aktual), dan persentase terpakai. [totalBudget] 0 = bulan itu tidak punya
 * budget — layer presentasi menampilkan status netral AMAN.
 */
data class BudgetHealth(
    val totalBudget: Long,
    val sisa: Long,
    val usedPercent: Float,
)
