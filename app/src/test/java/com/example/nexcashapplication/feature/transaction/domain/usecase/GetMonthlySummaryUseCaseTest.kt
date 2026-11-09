package com.example.nexcashapplication.feature.transaction.domain.usecase

import com.example.nexcashapplication.feature.dashboard.domain.model.MonthlySummary
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetMonthlySummaryUseCaseTest {

    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var useCase: GetMonthlySummaryUseCase

    @BeforeEach
    fun setUp() {
        transactionRepository = FakeTransactionRepository()
        useCase = GetMonthlySummaryUseCase(transactionRepository)
    }

    @Test
    fun `mengambil ringkasan bulanan sesuai parameter yearMonth`() = runTest {
        val summary = MonthlySummary(
            totalPemasukan = 10_000_000L,
            totalPengeluaran = 4_000_000L,
            totalSaldo = 6_000_000L,
            yearMonth = "2026-11",
        )
        transactionRepository.seedMonthlySummary(summary)

        val result = useCase("2026-11").first()

        assertEquals(10_000_000L, result.totalPemasukan)
        assertEquals(4_000_000L, result.totalPengeluaran)
        assertEquals(6_000_000L, result.totalSaldo)
        assertEquals("2026-11", result.yearMonth)
    }
}
