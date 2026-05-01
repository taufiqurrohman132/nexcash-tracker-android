package com.example.nexcashapplication.feature.dashboard.domain.model

// Gabungan budget + realisasi (dipakai di widget sisa anggaran)
data class BudgetSummary(
    val limitAmount: Long,
    val spentAmount: Long,
) {
    val remainingAmount: Long get() = limitAmount - spentAmount  // ✅

    val percentage: Float get() = if (limitAmount == 0L) 0f
    else (spentAmount.toFloat() / limitAmount) * 100  // ✅
}