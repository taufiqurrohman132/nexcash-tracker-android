package com.example.nexcashapplication.feature.statistics.presentation.main

import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import com.example.nexcashapplication.feature.budgets.domain.usecase.FakeBudgetRepository
import com.example.nexcashapplication.feature.category.presentation.main.FakeCategoryRepository
import com.example.nexcashapplication.feature.category.presentation.main.FakeTransactionRepository
import com.example.nexcashapplication.feature.statistics.domain.usecase.GetBudgetHealthUseCase
import com.example.nexcashapplication.feature.statistics.domain.usecase.GetCategoryAllocationUseCase
import com.example.nexcashapplication.feature.statistics.domain.usecase.GetExpenseSummaryUseCase
import com.example.nexcashapplication.feature.statistics.domain.usecase.GetSavingsRateUseCase
import com.example.nexcashapplication.feature.statistics.presentation.main.RealisasiTipe
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.YearMonth
import kotlin.time.Instant

/**
 * Unit test [StatistikViewModel]:
 * - kartu sisa anggaran/tingkat tabungan/total alokasi terisi — regresi lama:
 *   state-nya tidak pernah di-update sehingga selalu angka default (Rp0/0%/AMAN)
 * - ganti bulan ikut reload data; tab Alokasi/Realisasi mengubah sumber donut
 * - status anggaran OVER / WASPADA / AMAN netral sesuai ambang
 * - OnUbahAlokasiClick mengirim effect (regresi handler kosong)
 *
 * Dispatcher: Dispatchers.Main diganti StandardTestDispatcher; semua coroutine
 * ViewModel berjalan di scheduler virtual yang sama.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StatistikViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var budgetRepository: FakeBudgetRepository
    private lateinit var categoryRepository: FakeCategoryRepository
    private lateinit var transactionRepository: FakeTransactionRepository

    private val bulanIni = YearMonth.now()
    private val bulanLalu = bulanIni.minusMonths(1)

    @BeforeEach
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        budgetRepository = FakeBudgetRepository()
        categoryRepository = FakeCategoryRepository()
        transactionRepository = FakeTransactionRepository()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── State terisi (regresi selalu default) ────────────────────────────

    @Test
    fun `semua kartu terisi - bukan angka default`() = runTest(testDispatcher) {
        seedDasar()

        val vm = createViewModel()
        advanceUntilIdle()

        val state = vm.state.value
        assertEquals(2, state.alokasiDana.size) // kategori kosong tidak ikut
        assertEquals(2_000_000L, state.totalAlokasi) // alokasi 1,4jt + 0,6jt
        assertEquals(250_000L, state.totalPengeluaran)
        assertEquals(-37.5f, state.pengeluaranTrendPercent, 0.001f) // 250rb vs 400rb
        assertEquals(75f, state.tingkatTabungan, 0.001f) // (1jt − 250rb) / 1jt
        assertEquals(25f, state.tabunganTrendPercent, 0.001f) // 75 − 50 poin
        assertEquals(1_750_000L, state.sisaAnggaran) // 2jt − 250rb
        assertEquals(BudgetStatus.AMAN, state.statusAnggaran) // terpakai 12,5%
    }

    @Test
    fun `tanpa budget - sisa 0 dan status AMAN netral`() = runTest(testDispatcher) {
        categoryRepository.seed(category("c1", "Makanan"))
        transactionRepository.seed(tx("expense", 250_000, TransactionType.EXPENSE, bulanIni, day = 15))

        val vm = createViewModel()
        advanceUntilIdle()

        val state = vm.state.value
        assertEquals(0L, state.sisaAnggaran) // tanpa OVER palsu
        assertEquals(BudgetStatus.AMAN, state.statusAnggaran)
        assertEquals(0L, state.totalAlokasi)
    }

    // ── Status anggaran (ambang yang dipilih) ────────────────────────────

    @Test
    fun `status anggaran - OVER saat pengeluaran melebihi budget`() = runTest(testDispatcher) {
        categoryRepository.seed(category("c1", "Makanan"))
        budgetRepository.seed(budget("b1", "c1", 100_000))
        transactionRepository.seed(tx("expense", 250_000, TransactionType.EXPENSE, bulanIni, day = 15))

        val vm = createViewModel()
        advanceUntilIdle()

        assertEquals(-150_000L, vm.state.value.sisaAnggaran)
        assertEquals(BudgetStatus.OVER, vm.state.value.statusAnggaran)
    }

    @Test
    fun `status anggaran - WASPADA saat sisa tinggal di bawah 20 persen`() =
        runTest(testDispatcher) {
            categoryRepository.seed(category("c1", "Makanan"))
            budgetRepository.seed(budget("b1", "c1", 300_000))
            transactionRepository.seed(tx("expense", 250_000, TransactionType.EXPENSE, bulanIni, day = 15))

            val vm = createViewModel()
            advanceUntilIdle()

            // terpakai 83,3% → sisa 50rb < 20% dari 300rb
            assertEquals(BudgetStatus.WASPADA, vm.state.value.statusAnggaran)
        }

    // ── Interaksi ────────────────────────────────────────────────────────

    @Test
    fun `OnAlokasiTabChange - realisasi memakai pengeluaran aktual`() = runTest(testDispatcher) {
        seedDasar()

        val vm = createViewModel()
        advanceUntilIdle()

        vm.onEvent(StatistikEvent.OnAlokasiTabChange(AlokasiMode.REALISASI))
        advanceUntilIdle()

        val state = vm.state.value
        assertEquals(AlokasiMode.REALISASI, state.alokasiMode)
        assertEquals(listOf("c1"), state.alokasiDana.map { it.kategoriId })
        assertEquals(250_000L, state.totalAlokasi) // pengeluaran bulan ini
    }

    @Test
    fun `OnRealisasiTipeChange - pendapatan memakai pemasukan per kategori`() =
        runTest(testDispatcher) {
            seedDasar()

            val vm = createViewModel()
            advanceUntilIdle()

            vm.onEvent(StatistikEvent.OnAlokasiTabChange(AlokasiMode.REALISASI))
            vm.onEvent(StatistikEvent.OnRealisasiTipeChange(RealisasiTipe.PENDAPATAN))
            advanceUntilIdle()

            val state = vm.state.value
            assertEquals(AlokasiMode.REALISASI, state.alokasiMode)
            assertEquals(RealisasiTipe.PENDAPATAN, state.realisasiTipe)
            // pemasukan punya kategori sendiri di seedDasar (categoryId null) →
            // tidak ada irisan, total ikut 0
            assertTrue(state.alokasiDana.isEmpty())
            assertEquals(0L, state.totalAlokasi)

            // kembali ke pengeluaran → donut expense muncul lagi
            vm.onEvent(StatistikEvent.OnRealisasiTipeChange(RealisasiTipe.PENGELUARAN))
            advanceUntilIdle()

            assertEquals(listOf("c1"), vm.state.value.alokasiDana.map { it.kategoriId })
            assertEquals(250_000L, vm.state.value.totalAlokasi)
        }

    @Test
    fun `bulan - picker membuka, memilih menutup dan reload data`() = runTest(testDispatcher) {
        seedDasar()

        val vm = createViewModel()
        advanceUntilIdle()

        vm.onEvent(StatistikEvent.OnGantiBulanClick)
        assertTrue(vm.state.value.showMonthPicker)

        vm.onEvent(StatistikEvent.OnMonthSelected(bulanLalu))
        advanceUntilIdle()
        assertFalse(vm.state.value.showMonthPicker)
        assertEquals(bulanLalu, vm.state.value.selectedMonth)
        assertEquals(400_000L, vm.state.value.totalPengeluaran) // data ikut reload

        vm.onEvent(StatistikEvent.OnGantiBulanClick)
        vm.onEvent(StatistikEvent.OnMonthPickerDismiss)
        assertFalse(vm.state.value.showMonthPicker)
    }

    @Test
    fun `OnUbahAlokasiClick - mengirim effect navigasi`() = runTest(testDispatcher) {
        val vm = createViewModel()
        val effects = collectEffects(vm)
        advanceUntilIdle()

        vm.onEvent(StatistikEvent.OnUbahAlokasiClick)
        advanceUntilIdle()

        assertEquals(
            listOf<StatistikEffect>(StatistikEffect.NavigateToUbahAlokasi),
            effects,
        )
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    /**
     * Budget 2jt (c1 1,4jt + c2 0,6jt); bulan ini pemasukan 1jt & pengeluaran
     * 250rb (rate 75%); bulan lalu 800rb & 400rb (rate 50%, tren +25 poin).
     */
    private fun seedDasar() {
        categoryRepository.seed(
            category("c1", "Makanan"),
            category("c2", "Transportasi"),
            category("c3", "Kosong"),
        )
        budgetRepository.seed(
            budget("b1", "c1", 1_400_000),
            budget("b2", "c2", 600_000),
        )
        transactionRepository.seed(
            tx("income-now", 1_000_000, TransactionType.INCOME, bulanIni, day = 10),
            tx("expense-now", 250_000, TransactionType.EXPENSE, bulanIni, day = 15),
            tx("income-prev", 800_000, TransactionType.INCOME, bulanLalu, day = 10),
            tx("expense-prev", 400_000, TransactionType.EXPENSE, bulanLalu, day = 15),
        )
    }

    /** Kumpulkan semua effect selama test berjalan (collector di background scope). */
    private fun kotlinx.coroutines.test.TestScope.collectEffects(
        vm: StatistikViewModel,
    ): MutableList<StatistikEffect> {
        val effects = mutableListOf<StatistikEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.effect.collect { effects += it }
        }
        return effects
    }

    private fun createViewModel() = StatistikViewModel(
        getCategoryAllocationUseCase = GetCategoryAllocationUseCase(
            budgetRepository, transactionRepository, categoryRepository,
        ),
        getExpenseSummaryUseCase = GetExpenseSummaryUseCase(transactionRepository),
        getSavingsRateUseCase = GetSavingsRateUseCase(transactionRepository),
        getBudgetHealthUseCase = GetBudgetHealthUseCase(budgetRepository, transactionRepository),
    )

    private fun category(id: String, name: String) = Category(
        id = id,
        name = name,
        type = TypeCategory.EXPENSE,
        iconId = "ic",
        color = "#FF6B6B",
    )

    private fun budget(id: String, categoryId: String, amount: Long) = Budget(
        id = id,
        categoryId = categoryId,
        startDate = bulanIni.atDay(1),
        endDate = bulanIni.atEndOfMonth(),
        amount = amount,
        recurrenceType = RecurrenceType.CUSTOM,
        isRecurring = false,
    )

    private fun tx(
        id: String,
        amount: Long,
        type: TransactionType,
        month: YearMonth,
        day: Int,
    ) = Transaction(
        id = id,
        title = "tx-$id",
        amount = amount,
        categoryId = if (type == TransactionType.EXPENSE) "c1" else null,
        description = null,
        dateTime = Instant.fromEpochMilliseconds(
            month.atDay(day).atTime(12, 0).toInstant(java.time.ZoneOffset.UTC).toEpochMilli(),
        ),
        typeTransaction = type,
        accountId = "acc1",
    )
}
