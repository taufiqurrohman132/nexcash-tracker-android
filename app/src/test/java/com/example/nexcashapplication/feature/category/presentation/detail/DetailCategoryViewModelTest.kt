package com.example.nexcashapplication.feature.category.presentation.detail

import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.core.ui.UiState
import com.example.nexcashapplication.core.ui.dataOrNull
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import com.example.nexcashapplication.feature.budgets.domain.usecase.FakeBudgetRepository
import com.example.nexcashapplication.feature.budgets.domain.usecase.GetBudgetPeriodUseCase
import com.example.nexcashapplication.feature.category.domain.model.WeeklyTrend
import com.example.nexcashapplication.feature.category.domain.usecase.CategoryUseCases
import com.example.nexcashapplication.feature.category.domain.usecase.DeleteCategoryUseCase
import com.example.nexcashapplication.feature.category.domain.usecase.GetCategoryByIdUseCase
import com.example.nexcashapplication.feature.category.presentation.main.FakeCategoryRepository
import com.example.nexcashapplication.feature.category.presentation.main.FakeTransactionRepository
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
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
 * Unit test [DetailCategoryViewModel]:
 * - load detail: kategori + subkategori + transaksi terakhir + tren mingguan
 * - budget aktif (CUSTOM window ±2 hari) → totalBudget/usedBudget pada kategori
 * - regresi: re-emit Room tidak mengosongkan tren & transaksi terakhir
 *   (bug yang diperbaiki pada commit grafik tren mingguan)
 * - event → effect navigasi, termasuk jembatan "Lihat Semua" → NavigateToAllTransactions
 *
 * Route dipakai lewat konstruktor `internal` [DetailCategoryViewModel] dengan
 * [Route.CategoryRoute.Detail] langsung. Konstruktor produksi `@Inject` tetap membaca
 * SavedStateHandle.toRoute() di device; toRoute() tidak bisa berjalan di JVM unit test
 * karena Bundle Android di-mock (getString selalu null).
 *
 * Dispatcher: Dispatchers.Main diganti StandardTestDispatcher; semua coroutine
 * ViewModel berjalan di scheduler virtual yang sama (waktu nyata tidak dipakai).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DetailCategoryViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var categoryRepository: FakeCategoryRepository
    private lateinit var budgetRepository: FakeBudgetRepository
    private lateinit var transactionRepository: FakeTransactionRepository

    private val today: LocalDate = LocalDate.now()
    private val categoryId = "c1"

    @BeforeEach
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        categoryRepository = FakeCategoryRepository()
        budgetRepository = FakeBudgetRepository()
        transactionRepository = FakeTransactionRepository()

        categoryRepository.seed(
            category(categoryId, "Makanan"),
            category("c1-sub", "Makan Siang", parentId = categoryId),
            category("c2", "Transportasi"),
        )

        // t1 & t2 milik c1 (t1 lebih baru), t3 milik c2 → wajib tidak ikut
        transactionRepository.seedWithDetails(
            withDetails("t1", categoryId, hoursAgo(1)),
            withDetails("t2", categoryId, hoursAgo(25)),
            withDetails("t3", "c2", hoursAgo(2)),
        )
        transactionRepository.seed(
            transaction(categoryId, 150L, TransactionType.EXPENSE, Clock.System.now()),
        )

        categoryRepository.seedWeeklyTrend(
            WeeklyTrend(data = listOf(10f, 20f), labels = listOf("Sen", "Sel")),
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Load ─────────────────────────────────────────────────────────────

    @Test
    fun `memuat detail - kategori, subkategori, transaksi terakhir, dan tren masuk state`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            val data = vm.state.value.dataState.dataOrNull
            assertTrue(vm.state.value.dataState is UiState.Success)
            assertEquals("Makanan", data?.category?.name)
            assertEquals(listOf("c1-sub"), data?.subCategories?.map { it.id })
            // urut terbaru; t3 (kategori c2) tidak ikut; limit 5
            assertEquals(listOf("t1", "t2"), data?.recentTransactions?.map { it.id })
            assertEquals(listOf(10f, 20f), data?.weeklyData)
            assertEquals(listOf("Sen", "Sel"), data?.weeklyLabels)
        }

    @Test
    fun `budget aktif - totalBudget dan usedBudget terpasang pada kategori`() =
        runTest(testDispatcher) {
            budgetRepository.seed(budget("b1", categoryId, 500_000L))

            val vm = createViewModel()
            advanceUntilIdle()

            val data = vm.state.value.dataState.dataOrNull
            assertEquals(500_000L, data?.category?.totalBudget)
            // 150 (seed EXPENSE dalam window CUSTOM hari ini ± 2 hari)
            assertEquals(150L, data?.category?.usedBudget)
        }

    @Test
    fun `tanpa budget - totalBudget dan usedBudget nol`() = runTest(testDispatcher) {
        val vm = createViewModel()
        advanceUntilIdle()

        val data = vm.state.value.dataState.dataOrNull
        assertEquals(0L, data?.category?.totalBudget)
        assertEquals(0L, data?.category?.usedBudget)
    }

    @Test
    fun `regresi - re-emit kategori tidak mengosongkan tren dan transaksi terakhir`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            // pastikan terisi lebih dulu
            var data = vm.state.value.dataState.dataOrNull
            assertEquals(listOf(10f, 20f), data?.weeklyData)
            assertEquals(listOf("t1", "t2"), data?.recentTransactions?.map { it.id })

            // re-emit Room (ganti kategori lain) → combine membangun ulang UiModel;
            // tren & transaksi terakhir wajib dipertahankan, bukan di-reset
            categoryRepository.seed(category("c3", "Baru"))
            advanceUntilIdle()

            data = vm.state.value.dataState.dataOrNull
            assertEquals("Makanan", data?.category?.name)
            assertEquals(listOf(10f, 20f), data?.weeklyData)
            assertEquals(listOf("Sen", "Sel"), data?.weeklyLabels)
            assertEquals(listOf("t1", "t2"), data?.recentTransactions?.map { it.id })
        }

    @Test
    fun `gagal memuat kategori - dataState berubah ke Error`() = runTest(testDispatcher) {
        categoryRepository.throwOnLoad = RuntimeException("gagal")

        val vm = createViewModel()
        advanceUntilIdle()

        val state = vm.state.value.dataState
        assertTrue(state is UiState.Error)
        assertEquals("gagal", (state as UiState.Error).errorMessage)
    }

    // ── Event → effect ───────────────────────────────────────────────────

    @Test
    fun `OnBackClick mengirim NavigateBack`() = runTest(testDispatcher) {
        val vm = createViewModel()
        val effects = collectEffects(vm)
        advanceUntilIdle()

        vm.onEvent(DetailCategoryEvent.OnBackClick)
        advanceUntilIdle()

        assertEquals(listOf<DetailCategoryEffect>(DetailCategoryEffect.NavigateBack), effects)
    }

    @Test
    fun `OnEditClick mengirim NavigateToEditCategory dengan id kategori`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            val effects = collectEffects(vm)
            advanceUntilIdle()

            vm.onEvent(DetailCategoryEvent.OnEditClick)
            advanceUntilIdle()

            assertEquals(
                listOf(DetailCategoryEffect.NavigateToEditCategory(categoryId)),
                effects,
            )
        }

    @Test
    fun `OnLoadMoreTransactionsClick mengirim NavigateToAllTransactions - jembatan ke halaman Semua Transaksi`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            val effects = collectEffects(vm)
            advanceUntilIdle()

            vm.onEvent(DetailCategoryEvent.OnLoadMoreTransactionsClick)
            advanceUntilIdle()

            assertEquals(
                listOf(DetailCategoryEffect.NavigateToAllTransactions(categoryId)),
                effects,
            )
        }

    @Test
    fun `OnSeeAllSubCategoryClick mengirim NavigateToAllSubCategories dengan nama kategori`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            val effects = collectEffects(vm)
            advanceUntilIdle() // state harus Success dulu supaya nama tersedia

            vm.onEvent(DetailCategoryEvent.OnSeeAllSubCategoryClick)
            advanceUntilIdle()

            assertEquals(
                listOf(
                    DetailCategoryEffect.NavigateToAllSubCategories(categoryId, "Makanan"),
                ),
                effects,
            )
        }

    @Test
    fun `OnAddSubCategoryClick mengirim NavigateToAddSubCategory dengan parentId`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            val effects = collectEffects(vm)
            advanceUntilIdle()

            vm.onEvent(DetailCategoryEvent.OnAddSubCategoryClick)
            advanceUntilIdle()

            assertEquals(
                listOf(DetailCategoryEffect.NavigateToAddSubCategory(categoryId)),
                effects,
            )
        }

    @Test
    fun `OnSubCategoryClick - dengan id navigasi, tanpa id snackbar`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            val effects = collectEffects(vm)
            advanceUntilIdle()

            vm.onEvent(DetailCategoryEvent.OnSubCategoryClick("c1-sub"))
            vm.onEvent(DetailCategoryEvent.OnSubCategoryClick())
            advanceUntilIdle()

            assertEquals(
                listOf(
                    DetailCategoryEffect.NavigateToSubCategoryDetail("c1-sub"),
                    DetailCategoryEffect.ShowSnackbar("Sub Category ID is null"),
                ),
                effects,
            )
        }

    @Test
    fun `OnTransactionClick - dengan id navigasi, tanpa id snackbar`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            val effects = collectEffects(vm)
            advanceUntilIdle()

            vm.onEvent(DetailCategoryEvent.OnTransactionClick("t1"))
            vm.onEvent(DetailCategoryEvent.OnTransactionClick())
            advanceUntilIdle()

            assertEquals(
                listOf(
                    DetailCategoryEffect.NavigateToTransactionDetail("t1"),
                    DetailCategoryEffect.ShowSnackbar("Transaction ID is null"),
                ),
                effects,
            )
        }

    @Test
    fun `OnDeleteClick mengirim NavigateToDeleteCategory dengan tipe kategori`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            val effects = collectEffects(vm)
            advanceUntilIdle() // tipe diambil dari kategori yang sudah termuat

            vm.onEvent(DetailCategoryEvent.OnDeleteClick)
            advanceUntilIdle()

            assertEquals(
                listOf(
                    DetailCategoryEffect.NavigateToDeleteCategory(categoryId, TypeCategory.EXPENSE),
                ),
                effects,
            )
        }

    @Test
    fun `OnEditBudgetClick mengirim NavigateToEditBudget dengan id kategori`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            val effects = collectEffects(vm)
            advanceUntilIdle()

            vm.onEvent(DetailCategoryEvent.OnEditBudgetClick)
            advanceUntilIdle()

            assertEquals(
                listOf(DetailCategoryEffect.NavigateToEditBudget(categoryId = categoryId)),
                effects,
            )
        }

    @Test
    fun `OnAddTransactionClick dan OnAddNoteClick hari ini tidak mengirim efek`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            val effects = collectEffects(vm)
            advanceUntilIdle()

            vm.onEvent(DetailCategoryEvent.OnAddTransactionClick)
            vm.onEvent(DetailCategoryEvent.OnAddNoteClick)
            advanceUntilIdle()

            assertTrue(effects.isEmpty())
        }

    // ── Helpers ──────────────────────────────────────────────────────────

    /** Kumpulkan semua effect selama test berjalan (collector di background scope). */
    private fun kotlinx.coroutines.test.TestScope.collectEffects(
        vm: DetailCategoryViewModel,
    ): MutableList<DetailCategoryEffect> {
        val effects = mutableListOf<DetailCategoryEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.effect.collect { effects += it }
        }
        return effects
    }

    private fun createViewModel() = DetailCategoryViewModel(
        route = Route.CategoryRoute.Detail(categoryId),
        transactionRepository = transactionRepository,
        budgetRepository = budgetRepository,
        categoryRepository = categoryRepository,
        categoryUseCases = CategoryUseCases(
            getCategoryById = GetCategoryByIdUseCase(categoryRepository),
            deleteCategory = DeleteCategoryUseCase(categoryRepository),
        ),
        getBudgetPeriodUseCase = GetBudgetPeriodUseCase(),
    )

    private fun category(id: String, name: String, parentId: String? = null) = Category(
        id = id,
        parentId = parentId,
        name = name,
        type = TypeCategory.EXPENSE,
        iconId = "pet",
        color = "#FF0000",
    )

    private fun budget(id: String, catId: String, amount: Long) = Budget(
        id = id,
        categoryId = catId,
        startDate = today.minusDays(2),
        endDate = today.plusDays(2),
        amount = amount,
        recurrenceType = RecurrenceType.CUSTOM,
        isRecurring = false,
    )

    private fun withDetails(id: String, catId: String, dateTime: Instant) =
        TransactionWithDetails(
            transaction = Transaction(
                id = id,
                title = "tx-$id",
                amount = 25_000,
                categoryId = catId,
                description = null,
                dateTime = dateTime,
                typeTransaction = TransactionType.EXPENSE,
                accountId = "acc1",
            ),
        )

    private fun transaction(
        catId: String,
        amount: Long,
        type: TransactionType,
        dateTime: Instant,
    ) = Transaction(
        title = "tx-$catId",
        amount = amount,
        categoryId = catId,
        description = null,
        dateTime = dateTime,
        typeTransaction = type,
        accountId = "acc1",
    )

    private fun hoursAgo(hours: Long): Instant =
        Instant.fromEpochMilliseconds(
            Clock.System.now().toEpochMilliseconds() - hours * 60 * 60 * 1000,
        )
}
