package com.example.nexcashapplication.feature.dashboard.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MonthlySummaryTest {

    @Test
    fun `membuat MonthlySummary dengan nilai yang sesuai`() {
        val summary = MonthlySummary(
            totalPemasukan = 5_000_000L,
            totalPengeluaran = 2_000_000L,
            totalSaldo = 3_000_000L,
            yearMonth = "2026-11",
        )

        assertEquals(5_000_000L, summary.totalPemasukan)
        assertEquals(2_000_000L, summary.totalPengeluaran)
        assertEquals(3_000_000L, summary.totalSaldo)
        assertEquals("2026-11", summary.yearMonth)
    }

    @Test
    fun `copy memperbarui properti dengan benar`() {
        val original = MonthlySummary(
            totalPemasukan = 1_000_000L,
            totalPengeluaran = 400_000L,
            totalSaldo = 600_000L,
            yearMonth = "2026-10",
        )
        val updated = original.copy(totalSaldo = 700_000L)

        assertEquals(700_000L, updated.totalSaldo)
        assertEquals(original.totalPemasukan, updated.totalPemasukan)
        assertEquals(original.yearMonth, updated.yearMonth)
    }
}
