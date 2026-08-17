package com.example.nexcashapplication.core.datasource.local.mapper

import com.example.nexcashapplication.core.datasource.local.result.MonthlySummaryResult
import com.example.nexcashapplication.feature.dashboard.domain.model.MonthlySummary

fun MonthlySummaryResult.toDomain(yearMonth: String): MonthlySummary {
    return MonthlySummary(
        totalPemasukan = this.totalPemasukan,
        totalPengeluaran = this.totalPengeluaran,
        totalSaldo = this.totalSaldo,
        yearMonth = yearMonth,
    )
}