package com.example.nexcashapplication.feature.statistics.domain.usecase

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
 * Unit test [GetExpenseSummaryUseCase] — tren "Total Pengeluaran":
 * - regresi lama: `previousMonthOf(monthStart, monthEnd)` mengisi parameter
 *   (bulan, tahun) dengan millis → "bulan lalu" nyaris bulan ini → tren selalu ~0%
 * - regresi lama: pembagian Long membuang pecahan → 150rb vs 100rb jadi 0%, bukan 50%
 * - bulan lalu tanpa data → 0% (bukan pembagian nol)
 */
class GetExpenseSummaryUseCaseTest {

    private lateinit var transactionRepository: FakeTransactionRepository

    @BeforeEach
    fun setUp() {
        transactionRepository = FakeTransactionRepository()
    }

    @Test
    fun `tren - dibanding bulan lalu dengan pembagian presisi`() = runTest {
        transactionRepository.seed(
            tx("sept", 150_000, TransactionType.EXPENSE, year = 2026, month = 9),
            tx("agustus", 100_000, TransactionType.EXPENSE, year = 2026, month = 8),
        )

        val summary = GetExpenseSummaryUseCase(transactionRepository)(YearMonth.of(2026, 9)).first()

        assertEquals(150_000L, summary.total)
        // (150_000 − 100_000) / 100_000 × 100 = 50 — kode lama: range salah + Long → 0
        assertEquals(50f, summary.percentChangeFromLastMonth, 0.001f)
    }

    @Test
    fun `tren - pengeluaran turun hasilnya negatif`() = runTest {
        transactionRepository.seed(
            tx("sept", 50_000, TransactionType.EXPENSE, year = 2026, month = 9),
            tx("agustus", 100_000, TransactionType.EXPENSE, year = 2026, month = 8),
        )

        val summary = GetExpenseSummaryUseCase(transactionRepository)(YearMonth.of(2026, 9)).first()

        assertEquals(-50f, summary.percentChangeFromLastMonth, 0.001f)
    }

    @Test
    fun `tren - bulan lalu tanpa pengeluaran hasilnya 0`() = runTest {
        transactionRepository.seed(
            tx("sept", 150_000, TransactionType.EXPENSE, year = 2026, month = 9),
        )

        val summary = GetExpenseSummaryUseCase(transactionRepository)(YearMonth.of(2026, 9)).first()

        assertEquals(150_000L, summary.total)
        assertEquals(0f, summary.percentChangeFromLastMonth, 0.001f)
    }

    private fun tx(id: String, amount: Long, type: TransactionType, year: Int, month: Int) =
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
