package com.example.nexcashapplication.feature.account.domain.model

data class MonthlyAccountSummary(
    val totalSaldoGabungan: Long,
    val percentage: Double,
    val activeAccount: Int,
)
