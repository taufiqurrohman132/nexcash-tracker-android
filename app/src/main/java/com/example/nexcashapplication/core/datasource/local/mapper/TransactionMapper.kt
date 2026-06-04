package com.example.nexcashapplication.core.datasource.local.mapper

import com.example.nexcashapplication.core.datasource.local.result.MonthlySummaryResult
import com.example.nexcashapplication.feature.dashboard.domain.model.MonthlySummary

fun MonthlySummaryResult.toDomain(bulan: Int, tahun: Int): MonthlySummary {
    return MonthlySummary(
        totalPemasukan = this.totalPemasukan,
        totalPengeluaran = this.totalPengeluaran,
        totalSaldo = this.totalSaldo,
        bulan = bulan,
        tahun = tahun
    )
}