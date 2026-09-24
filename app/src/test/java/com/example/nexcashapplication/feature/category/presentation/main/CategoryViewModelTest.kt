package com.example.nexcashapplication.feature.category.presentation.main

import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import com.example.nexcashapplication.feature.budgets.domain.usecase.FakeBudgetRepository
import com.example.nexcashapplication.feature.budgets.domain.usecase.GetBudgetPeriodUseCase
import com.example.nexcashapplication.feature.category.domain.usecase.CategoryUseCases
import com.example.nexcashapplication.feature.category.domain.usecase.DeleteCategoryUseCase
import com.example.nexcashapplication.feature.category.domain.usecase.GetCategoryByIdUseCase
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Unit test [CategoryViewModel]:
 * - load expense/income list (budget aktif + terpakai pada window periode)
 * - event tab/page/search  → perubahan state
 * - event klik kategori/tambah → effect navigasi (sekali-kali)
 * - search dengan debounce 300ms (virtual time via runTest)
 *
 * Dispatcher: Dispatchers.Main diganti StandardTestDispatcher; semua coroutine
 * ViewModel berjalan di scheduler virtual yang sama sehingga debounce tidak
 * memakai waktu nyata.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CategoryViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var categoryRepository: FakeCategoryRepository
    private lateinit var budgetRepository: FakeBudgetRepository
    private lateinit var transactionRepository: FakeTransactionRepository

    private val today: LocalDate = LocalDate.now()

    @BeforeEach
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        categoryRepository = FakeCategoryRepository()
        budgetRepository = FakeBudgetRepository()
        transactionRepository = FakeTransactionRepository()

        categoryRepository.seed(
            category("c1", "Makanan", TypeCategory.EXPENSE),
            category("c2", "Transportasi", TypeCategory.EXPENSE),
            category("c3", "Gaji", TypeCategory.INCOME),
        )

        // Budget aktif (window hari ini ± 2 hari, tipe CUSTOM → window eksplisit,
        // tidak bergantung batas bulan sehingga test tidak goyah awal/bulan)
        budgetRepository.seed(
            budget("b1", "c1", amount = 1_000_000L),
            budget("b3", "c3", amount = 500_000L),
        )

        transactionRepository.seed(
            transaction("c1", 100L, TransactionType.EXPENSE, Clock.System.now()),
            transaction("c1", 50L, TransactionType.EXPENSE, Clock.System.now()),
            // 10 hari lalu → di luar window, wajib TIDAK ikut terpakai
            transaction("c1", 999L, TransactionType.EXPENSE, daysAgo(10)),
            transaction("c3", 5_000L, TransactionType.INCOME, Clock.System.now()),
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Load ─────────────────────────────────────────────────────────────

    @Test
    fun `memuat expense dan income list - budget aktif dan terpakai sesuai window`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            val expense = vm.state.value.expenseList
            assertEquals(listOf("c1", "c2"), expense.map { it.id })

            val makanan = expense.first { it.id == "c1" }
            assertEquals(1_000_000L, makanan.budget)
            // 100 + 50 dalam window; 999 (10 hari lalu) tidak dihitung
            assertEquals(150L, makanan.terpakai)

            // Tanpa budget → budget 0 & terpakai dipaksa 0
            val transport = expense.first { it.id == "c2" }
            assertEquals(0L, transport.budget)
            assertEquals(0L, transport.terpakai)

            val income = vm.state.value.incomeList
            assertEquals(listOf("c3"), income.map { it.id })
            assertEquals(500_000L, income.first().budget)
            assertEquals(5_000L, income.first().terpakai)
        }

    @Test
    fun `total anggaran dan sisa mengikuti tab aktif`() = runTest(testDispatcher) {
        val vm = createViewModel()
        advanceUntilIdle()

        // Default tab EXPENSE
        assertEquals(1_000_000L, vm.state.value.totalAnggaran)
        assertEquals(1_000_000L - 150L, vm.state.value.sisaAnggaran)

        vm.onEvent(CategoryEvent.OnTabCategoryClick(TypeCategory.INCOME))

        assertEquals(500_000L, vm.state.value.totalAnggaran)
        assertEquals(500_000L - 5_000L, vm.state.value.sisaAnggaran)
        assertEquals(5_000L, vm.state.value.totalPemasukan)
    }

    // ── Event → state ────────────────────────────────────────────────────

    @Test
    fun `OnTabCategoryClick dan OnPageChanged mengubah tab - fallback ke EXPENSE`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            vm.onEvent(CategoryEvent.OnTabCategoryClick(TypeCategory.INCOME))
            assertEquals(TypeCategory.INCOME, vm.state.value.selectedTabCategory)

            // index 0 = EXPENSE, 1 = INCOME (urutan enum)
            vm.onEvent(CategoryEvent.OnPageChanged(1))
            assertEquals(TypeCategory.INCOME, vm.state.value.selectedTabCategory)

            // index di luar jangkauan → fallback EXPENSE
            vm.onEvent(CategoryEvent.OnPageChanged(99))
            assertEquals(TypeCategory.EXPENSE, vm.state.value.selectedTabCategory)
        }

    @Test
    fun `OnSearchQueryChange langsung memperbarui query`() = runTest(testDispatcher) {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.onEvent(CategoryEvent.OnSearchQueryChange("ma"))

        assertEquals("ma", vm.state.value.searchQuery)
    }

    @Test
    fun `OnRefresh tidak membuat crash`() = runTest(testDispatcher) {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.onEvent(CategoryEvent.OnRefresh)
        advanceUntilIdle()

        // Belum ada logika refresh; yang dipastikan hanya tidak melempar
        assertEquals(listOf("c1", "c2"), vm.state.value.expenseList.map { it.id })
    }

    // ── Event → effect ───────────────────────────────────────────────────

    @Test
    fun `OnKategoriClick mengirim NavigateToDetail satu kali`() = runTest(testDispatcher) {
        val vm = createViewModel()
        val effects = mutableListOf<CategoryEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.effect.collect { effects += it }
        }
        advanceUntilIdle()

        vm.onEvent(CategoryEvent.OnKategoriClick("c1"))
        advanceUntilIdle()

        assertEquals(listOf<CategoryEffect>(CategoryEffect.NavigateToDetail("c1")), effects)
    }

    @Test
    fun `OnAddKategoriClick mengirim NavigateToAddCategory`() = runTest(testDispatcher) {
        val vm = createViewModel()
        val effects = mutableListOf<CategoryEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.effect.collect { effects += it }
        }
        advanceUntilIdle()

        vm.onEvent(CategoryEvent.OnAddKategoriClick)
        advanceUntilIdle()

        assertEquals(CategoryEffect.NavigateToAddCategory(), effects.last())
    }

    // ── Search (debounce 300ms, virtual time) ────────────────────────────

    @Test
    fun `pencarian ma pada tab EXPENSE menemukan Makanan saja`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            vm.onEvent(CategoryEvent.OnSearchQueryChange("ma"))
            advanceUntilIdle() // debounce 300ms dilewati di virtual time

            assertEquals(listOf("c1"), vm.state.value.categorySearchList.map { it.id })
        }

    @Test
    fun `pencarian mengikuti tab - ga di INCOME menemukan Gaji, di EXPENSE kosong`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            vm.onEvent(CategoryEvent.OnTabCategoryClick(TypeCategory.INCOME))
            vm.onEvent(CategoryEvent.OnSearchQueryChange("ga"))
            advanceUntilIdle()
            assertEquals(listOf("c3"), vm.state.value.categorySearchList.map { it.id })

            // Kembali ke tab EXPENSE + query sama → tidak ada match
            vm.onEvent(CategoryEvent.OnTabCategoryClick(TypeCategory.EXPENSE))
            advanceUntilIdle()
            assertTrue(vm.state.value.categorySearchList.isEmpty())
        }

    @Test
    fun `query dikosongkan - hasil pencarian direset`() = runTest(testDispatcher) {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.onEvent(CategoryEvent.OnSearchQueryChange("ma"))
        advanceUntilIdle()
        assertEquals(listOf("c1"), vm.state.value.categorySearchList.map { it.id })

        vm.onEvent(CategoryEvent.OnSearchQueryChange(""))
        advanceUntilIdle()
        assertTrue(vm.state.value.categorySearchList.isEmpty())
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private fun createViewModel() = CategoryViewModel(
        categoryUseCases = CategoryUseCases(
            getCategoryById = GetCategoryByIdUseCase(categoryRepository),
            deleteCategory = DeleteCategoryUseCase(categoryRepository),
        ),
        categoryRepository = categoryRepository,
        budgetRepository = budgetRepository,
        transactionRepository = transactionRepository,
        getBudgetPeriodUseCase = GetBudgetPeriodUseCase(),
    )

    private fun category(id: String, name: String, type: TypeCategory) = Category(
        id = id,
        name = name,
        type = type,
        iconId = "location",
        color = "#FF0000",
    )

    private fun budget(id: String, categoryId: String, amount: Long) = Budget(
        id = id,
        categoryId = categoryId,
        startDate = today.minusDays(2),
        endDate = today.plusDays(2),
        amount = amount,
        recurrenceType = RecurrenceType.CUSTOM,
        isRecurring = false,
    )

    private fun transaction(
        categoryId: String,
        amount: Long,
        type: TransactionType,
        dateTime: Instant,
    ) = Transaction(
        title = "tx-$categoryId",
        amount = amount,
        categoryId = categoryId,
        description = null,
        dateTime = dateTime,
        typeTransaction = type,
        accountId = "acc_1",
    )

    private fun daysAgo(days: Long): Instant =
        Instant.fromEpochMilliseconds(
            Clock.System.now().toEpochMilliseconds() - days * 24 * 60 * 60 * 1000,
        )
}
