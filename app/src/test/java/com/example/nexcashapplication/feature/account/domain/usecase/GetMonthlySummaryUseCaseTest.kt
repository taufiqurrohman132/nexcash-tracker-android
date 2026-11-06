package com.example.nexcashapplication.feature.account.domain.usecase

import com.example.nexcashapplication.feature.account.domain.model.MonthlySummaryAccount
import com.example.nexcashapplication.feature.transaction.domain.usecase.FakeAccountRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetMonthlySummaryUseCaseTest {

    private lateinit var accountRepository: FakeAccountRepository
    private lateinit var useCase: GetMonthlySummaryUseCase

    @BeforeEach
    fun setUp() {
        accountRepository = FakeAccountRepository()
        useCase = GetMonthlySummaryUseCase(accountRepository)
    }

    @Test
    fun `ringkasan bulanan - menghitung persentase perubahan saldo bersih dengan benar`() = runTest {
        // totalBalance = 1.000.000, income = 500.000, expense = 300.000
        // netChange = 200.000, startOfMonthlyBalance = 1.000.000 - 200.000 = 800.000
        // percentage = (200.000 * 100.0) / 800.000 = 25.0%
        accountRepository.monthlySummary.value = MonthlySummaryAccount(
            totalBalance = 1_000_000L,
            activeAccount = 3,
            monthlyIncome = 500_000L,
            monthlyExpense = 300_000L,
        )

        val summary = useCase().first()

        assertEquals(1_000_000L, summary.totalSaldoGabungan)
        assertEquals(3, summary.activeAccount)
        assertEquals(25.0, summary.percentage, 0.001)
    }

    @Test
    fun `ringkasan bulanan - saldo awal nol menghasilkan persentase nol`() = runTest {
        // totalBalance = 100.000, netChange = 100.000 (income 100k, expense 0)
        // startOfMonthlyBalance = 100.000 - 100.000 = 0 -> percentage = 0.0
        accountRepository.monthlySummary.value = MonthlySummaryAccount(
            totalBalance = 100_000L,
            activeAccount = 1,
            monthlyIncome = 100_000L,
            monthlyExpense = 0L,
        )

        val summary = useCase().first()

        assertEquals(100_000L, summary.totalSaldoGabungan)
        assertEquals(1, summary.activeAccount)
        assertEquals(0.0, summary.percentage, 0.001)
    }

    @Test
    fun `ringkasan bulanan - pengeluaran lebih besar menghasilkan persentase negatif`() = runTest {
        // totalBalance = 800.000, income = 100.000, expense = 300.000
        // netChange = -200.000, startOfMonthlyBalance = 800.000 - (-200.000) = 1.000.000
        // percentage = (-200.000 * 100.0) / 1.000.000 = -20.0%
        accountRepository.monthlySummary.value = MonthlySummaryAccount(
            totalBalance = 800_000L,
            activeAccount = 2,
            monthlyIncome = 100_000L,
            monthlyExpense = 300_000L,
        )

        val summary = useCase().first()

        assertEquals(800_000L, summary.totalSaldoGabungan)
        assertEquals(-20.0, summary.percentage, 0.001)
    }
}
