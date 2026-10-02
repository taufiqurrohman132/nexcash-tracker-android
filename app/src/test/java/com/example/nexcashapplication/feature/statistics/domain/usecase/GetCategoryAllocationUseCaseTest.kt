package com.example.nexcashapplication.feature.statistics.domain.usecase

import com.example.nexcashapplication.core.common.toEndOfMonthMillis
import com.example.nexcashapplication.core.common.toStartOfMonthMillis
import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import com.example.nexcashapplication.feature.budgets.domain.usecase.FakeBudgetRepository
import com.example.nexcashapplication.feature.category.presentation.main.FakeCategoryRepository
import com.example.nexcashapplication.feature.category.presentation.main.FakeTransactionRepository
import com.example.nexcashapplication.feature.statistics.presentation.main.AlokasiMode
import com.example.nexcashapplication.feature.statistics.presentation.main.RealisasiTipe
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
 * Unit test [GetCategoryAllocationUseCase] — donut "Alokasi Dana":
 * - regresi lama: persentase Long division `amount / totalAmount * 100` hampir
 *   selalu 0 → irisan & legend salah total; kini Float (1,4jt/2jt = 70%)
 * - kategori nominal 0 tidak ikut (dulu semua kategori tampil, legend penuh 0%)
 * - urut nominal menurun; REALISASI memakai pengeluaran aktual per kategori
 */
class GetCategoryAllocationUseCaseTest {

    private lateinit var budgetRepository: FakeBudgetRepository
    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var categoryRepository: FakeCategoryRepository

    private val bulan = YearMonth.of(2026, 9)

    @BeforeEach
    fun setUp() {
        budgetRepository = FakeBudgetRepository()
        transactionRepository = FakeTransactionRepository()
        categoryRepository = FakeCategoryRepository()
        categoryRepository.seed(
            category("c1", "Makanan"),
            category("c2", "Transportasi"),
            category("c3", "Kosong"), // tanpa budget & tanpa transaksi
        )
    }

    @Test
    fun `alokasi - hanya kategori bernilai, persentase presisi, urut menurun`() = runTest {
        budgetRepository.seed(
            budget("b1", "c1", 1_400_000),
            budget("b2", "c2", 600_000),
        )

        val list = useCase(AlokasiMode.ALOKASI).first()

        assertEquals(listOf("c1", "c2"), list.map { it.kategoriId }) // c3 (0) gugur
        assertEquals(70f, list[0].persentase, 0.001f) // versi Long → 0
        assertEquals(30f, list[1].persentase, 0.001f)
        assertEquals(1_400_000L, list[0].nominal)
    }

    @Test
    fun `realisasi - memakai pengeluaran aktual per kategori`() = runTest {
        transactionRepository.seed(
            tx("e1", 600_000, "c1"),
            tx("e2", 300_000, "c2"),
            tx("e3", 100_000, "c3"),
        )

        val list = useCase(AlokasiMode.REALISASI).first()

        assertEquals(listOf("c1", "c2", "c3"), list.map { it.kategoriId }) // urut menurun
        assertEquals(60f, list[0].persentase, 0.001f) // 600rb / 1jt
    }

    @Test
    fun `realisasi pengeluaran - nominal subkategori digulung ke induk`() = runTest {
        categoryRepository.seed(
            category("p1", "Makanan"),
            category("s1", "Bakso", parentId = "p1"),
        )
        transactionRepository.seed(
            tx("e1", 400_000, "p1"),
            tx("e2", 100_000, "s1"), // sub → masuk p1
            tx("e3", 300_000, "c2"),
        )

        val list = useCase(AlokasiMode.REALISASI).first()

        assertEquals(listOf("p1", "c2"), list.map { it.kategoriId }) // s1 tidak jadi irisan
        assertEquals(500_000L, list[0].nominal) // 400rb induk + 100rb sub
        assertEquals(62.5f, list[0].persentase, 0.001f) // 500rb / 800rb
    }

    @Test
    fun `realisasi pendapatan - memakai pemasukan, pengeluaran diabaikan`() = runTest {
        transactionRepository.seed(
            Transaction(
                id = "i1",
                title = "gaji",
                amount = 900_000,
                categoryId = "c1",
                description = null,
                dateTime = Instant.fromEpochMilliseconds(
                    LocalDate.of(2026, 9, 15).atTime(12, 0).toInstant(ZoneOffset.UTC).toEpochMilli(),
                ),
                typeTransaction = TransactionType.INCOME,
                accountId = "acc1",
            ),
            tx("e1", 600_000, "c1"), // expense tidak ikut donut pendapatan
            tx("e2", 300_000, "c2"),
        )

        val list = useCase(AlokasiMode.REALISASI, RealisasiTipe.PENDAPATAN).first()

        assertEquals(listOf("c1"), list.map { it.kategoriId }) // c2 cuma punya expense
        assertEquals(900_000L, list[0].nominal)
        assertEquals(100f, list[0].persentase, 0.001f) // 900rb / 900rb
    }

    private fun useCase(
        mode: AlokasiMode,
        tipe: RealisasiTipe = RealisasiTipe.PENGELUARAN,
    ) = GetCategoryAllocationUseCase(
        budgetRepository, transactionRepository, categoryRepository,
    )(
        bulan.toStartOfMonthMillis(),
        bulan.toEndOfMonthMillis(),
        mode,
        tipe,
    )

    private fun category(id: String, name: String, parentId: String? = null) = Category(
        id = id,
        parentId = parentId,
        name = name,
        type = TypeCategory.EXPENSE,
        iconId = "ic",
        color = "#FF6B6B",
    )

    private fun budget(id: String, categoryId: String, amount: Long) = Budget(
        id = id,
        categoryId = categoryId,
        startDate = LocalDate.of(2026, 9, 1),
        endDate = LocalDate.of(2026, 9, 30),
        amount = amount,
        recurrenceType = RecurrenceType.CUSTOM,
        isRecurring = false,
    )

    private fun tx(id: String, amount: Long, categoryId: String) = Transaction(
        id = id,
        title = "tx-$id",
        amount = amount,
        categoryId = categoryId,
        description = null,
        dateTime = Instant.fromEpochMilliseconds(
            LocalDate.of(2026, 9, 15).atTime(12, 0).toInstant(ZoneOffset.UTC).toEpochMilli(),
        ),
        typeTransaction = TransactionType.EXPENSE,
        accountId = "acc1",
    )
}
