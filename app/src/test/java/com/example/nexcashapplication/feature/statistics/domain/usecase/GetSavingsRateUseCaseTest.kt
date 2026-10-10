package com.example.nexcashapplication.feature.statistics.domain.usecase

import com.example.nexcashapplication.feature.category.presentation.main.FakeTransactionRepository
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.core.domain.model.TypeCategory
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
 * Unit test [GetSavingsRateUseCase] — kartu "Tingkat Tabungan" yang dulunya
 * selalu 0% (use case-nya hanya komentar):
 * - rate = (pemasukan − pengeluaran) / pemasukan × 100
 * - tren = selisih poin persen vs bulan lalu
 * - pemasukan ≤ 0 → 0; bulan lalu tanpa pemasukan → tren 0
 */
class GetSavingsRateUseCaseTest {

    private lateinit var transactionRepository: FakeTransactionRepository

    @BeforeEach
    fun setUp() {
        transactionRepository = FakeTransactionRepository()
    }

    @Test
    fun `tingkat tabungan - dihitung dari pemasukan dan pengeluaran dua bulan`() = runTest {
        transactionRepository.seed(
            // bulan ini: pemasukan 1jt, pengeluaran 250rb → 75%
            tx("income-sept", 1_000_000, TypeCategory.INCOME, year = 2026, month = 9),
            tx("expense-sept", 250_000, TypeCategory.EXPENSE, year = 2026, month = 9),
            // bulan lalu: pemasukan 800rb, pengeluaran 400rb → 50%
            tx("income-aug", 800_000, TypeCategory.INCOME, year = 2026, month = 8),
            tx("expense-aug", 400_000, TypeCategory.EXPENSE, year = 2026, month = 8),
        )

        val rate = GetSavingsRateUseCase(transactionRepository)(YearMonth.of(2026, 9)).first()

        assertEquals(75f, rate.rate, 0.001f)
        assertEquals(25f, rate.trendPercent, 0.001f) // 75 − 50 poin persen
    }

    @Test
    fun `tingkat tabungan - pemasukan nol hasilnya 0 tanpa pembagian nol`() = runTest {
        transactionRepository.seed(
            tx("expense-sept", 250_000, TypeCategory.EXPENSE, year = 2026, month = 9),
        )

        val rate = GetSavingsRateUseCase(transactionRepository)(YearMonth.of(2026, 9)).first()

        assertEquals(0f, rate.rate, 0.001f)
        assertEquals(0f, rate.trendPercent, 0.001f) // bulan lalu tanpa pemasukan
    }

    private fun tx(id: String, amount: Long, type: TypeCategory, year: Int, month: Int) =
        Transaction(
            id = id,
            title = "tx-$id",
            amount = amount,
            categoryId = "c1",
            description = null,
            dateTime = Instant.fromEpochMilliseconds(
                LocalDate.of(year, month, 15).atTime(12, 0).toInstant(ZoneOffset.UTC).toEpochMilli(),
            ),
            typeTransaction = type,
            accountId = "acc1",
        )
}
