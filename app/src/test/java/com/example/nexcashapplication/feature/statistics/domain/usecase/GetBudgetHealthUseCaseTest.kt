package com.example.nexcashapplication.feature.statistics.domain.usecase

import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import com.example.nexcashapplication.feature.budgets.domain.usecase.FakeBudgetRepository
import com.example.nexcashapplication.feature.category.presentation.main.FakeTransactionRepository
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset
import kotlin.time.Instant

/**
 * Unit test [GetBudgetHealthUseCase] — kartu "Sisa Anggaran" yang dulunya
 * selalu Rp0:
 * - total = semua budget yang menabrak periode, sisa = total − pengeluaran
 * - tanpa budget → total 0, usedPercent 0 (presentasi menampilkan AMAN netral)
 */
class GetBudgetHealthUseCaseTest {

    private lateinit var budgetRepository: FakeBudgetRepository
    private lateinit var transactionRepository: FakeTransactionRepository

    @BeforeEach
    fun setUp() {
        budgetRepository = FakeBudgetRepository()
        transactionRepository = FakeTransactionRepository()
    }

    @Test
    fun `sisa anggaran - total budget dikurangi pengeluaran bulan itu`() = runTest {
        budgetRepository.seed(
            budget("b1", "c1", 1_500_000),
            budget("b2", "c2", 500_000),
        )
        transactionRepository.seed(tx("e1", 700_000))

        val health = GetBudgetHealthUseCase(budgetRepository, transactionRepository)(
            YearMonth.of(2026, 9),
        ).first()

        assertEquals(2_000_000L, health.totalBudget)
        assertEquals(1_300_000L, health.sisa)
        assertEquals(35f, health.usedPercent, 0.001f) // 700rb / 2jt
    }

    @Test
    fun `tanpa budget - total 0 dan persentase 0`() = runTest {
        transactionRepository.seed(tx("e1", 700_000))

        val health = GetBudgetHealthUseCase(budgetRepository, transactionRepository)(
            YearMonth.of(2026, 9),
        ).first()

        assertEquals(0L, health.totalBudget)
        assertEquals(0f, health.usedPercent, 0.001f)
    }

    private fun budget(id: String, categoryId: String, amount: Long) = Budget(
        id = id,
        categoryId = categoryId,
        startDate = LocalDate.of(2026, 9, 1),
        endDate = LocalDate.of(2026, 9, 30),
        amount = amount,
        recurrenceType = RecurrenceType.CUSTOM,
        isRecurring = false,
    )

    private fun tx(id: String, amount: Long) = Transaction(
        id = id,
        title = "tx-$id",
        amount = amount,
        categoryId = "c1",
        description = null,
        dateTime = Instant.fromEpochMilliseconds(
            LocalDate.of(2026, 9, 15).atTime(12, 0).toInstant(ZoneOffset.UTC).toEpochMilli(),
        ),
        typeTransaction = TransactionType.EXPENSE,
        accountId = "acc1",
    )
}
