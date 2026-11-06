package com.example.nexcashapplication.feature.budgets.presentation.addedit

import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import com.example.nexcashapplication.feature.budgets.domain.usecase.DeleteBudgetUseCase
import com.example.nexcashapplication.feature.budgets.domain.usecase.EnsureUpToDateBudgetsUseCase
import com.example.nexcashapplication.feature.budgets.domain.usecase.FakeBudgetRepository
import com.example.nexcashapplication.feature.budgets.domain.usecase.GetBudgetPeriodUseCase
import com.example.nexcashapplication.feature.budgets.domain.usecase.SaveBudgetUseCase
import com.example.nexcashapplication.feature.category.presentation.main.FakeCategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditBudgetViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var budgetRepository: FakeBudgetRepository
    private lateinit var categoryRepository: FakeCategoryRepository
    private lateinit var getBudgetPeriodUseCase: GetBudgetPeriodUseCase
    private lateinit var saveBudgetUseCase: SaveBudgetUseCase
    private lateinit var deleteBudgetUseCase: DeleteBudgetUseCase
    private lateinit var ensureUpToDateBudgets: EnsureUpToDateBudgetsUseCase

    @BeforeEach
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        budgetRepository = FakeBudgetRepository()
        categoryRepository = FakeCategoryRepository()
        getBudgetPeriodUseCase = GetBudgetPeriodUseCase()
        ensureUpToDateBudgets = EnsureUpToDateBudgetsUseCase(budgetRepository)
        saveBudgetUseCase = SaveBudgetUseCase(budgetRepository, categoryRepository, getBudgetPeriodUseCase)
        deleteBudgetUseCase = DeleteBudgetUseCase(budgetRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun testCategory(
        id: String,
        name: String,
        type: TypeCategory = TypeCategory.EXPENSE,
        iconId: String = "icon_cat",
        color: String = "#FF0000",
    ) = Category(
        id = id,
        name = name,
        type = type,
        iconId = iconId,
        color = color,
    )

    private fun testBudget(
        id: String,
        categoryId: String,
        start: LocalDate = LocalDate.now().withDayOfMonth(1),
        end: LocalDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()),
        amount: Long = 500_000L,
        recurrenceType: RecurrenceType = RecurrenceType.MONTHLY,
        isRecurring: Boolean = true,
    ) = Budget(
        id = id,
        categoryId = categoryId,
        startDate = start,
        endDate = end,
        amount = amount,
        recurrenceType = recurrenceType,
        isRecurring = isRecurring,
    )

    private fun createViewModel(
        budgetId: String? = null,
        categoryId: String? = null,
    ) = AddEditBudgetViewModel(
        route = Route.BudgetRoute.AddEdit(budgetId = budgetId, categoryId = categoryId),
        budgetRepository = budgetRepository,
        categoryRepository = categoryRepository,
        getBudgetPeriodUseCase = getBudgetPeriodUseCase,
        saveBudgetUseCase = saveBudgetUseCase,
        deleteBudgetUseCase = deleteBudgetUseCase,
        ensureUpToDateBudgets = ensureUpToDateBudgets,
    )

    @Test
    fun `mode buat baru - inisialisasi default MONTHLY dan memuat info kategori`() = runTest(testDispatcher) {
        categoryRepository.seed(
            testCategory(id = "cat_1", name = "Makanan"),
        )

        val viewModel = createViewModel(categoryId = "cat_1")
        runCurrent()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertNull(state.budgetId)
        assertEquals("Makanan", state.kategoriNama)
        assertEquals(RecurrenceType.MONTHLY, state.periodeTipe)
        assertFalse(state.isTanggalEditable)
    }

    @Test
    fun `mode edit - memuat data budget yang sudah ada`() = runTest(testDispatcher) {
        categoryRepository.seed(
            testCategory(id = "cat_1", name = "Belanja"),
        )
        budgetRepository.seed(
            testBudget(id = "b1", categoryId = "cat_1", amount = 750_000L),
        )

        val viewModel = createViewModel(budgetId = "b1", categoryId = "cat_1")
        runCurrent()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals("b1", state.budgetId)
        assertEquals(750_000L, state.batasSebelumnya)
        assertEquals(750_000L, state.batasBaruParsed)
    }

    @Test
    fun `ubah batas nominal - memperbarui batasBaruParsed`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        runCurrent()

        viewModel.onEvent(AddEditBudgetEvent.BatasBaruChanged("1250000"))

        assertEquals(1_250_000L, viewModel.state.value.batasBaruParsed)
    }

    @Test
    fun `ubah tipe periode ke CUSTOM - tanggal menjadi editable dan switch berulang aktif`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        runCurrent()

        viewModel.onEvent(AddEditBudgetEvent.PeriodeTipeChanged(RecurrenceType.CUSTOM))

        val state = viewModel.state.value
        assertEquals(RecurrenceType.CUSTOM, state.periodeTipe)
        assertTrue(state.isTanggalEditable)
        assertTrue(state.isBerulangToggleEnabled)
    }

    @Test
    fun `ubah tipe periode ke WEEKLY - tanggal non-editable dan berulang aktif`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        runCurrent()

        viewModel.onEvent(AddEditBudgetEvent.PeriodeTipeChanged(RecurrenceType.WEEKLY))

        val state = viewModel.state.value
        assertEquals(RecurrenceType.WEEKLY, state.periodeTipe)
        assertFalse(state.isTanggalEditable)
        assertTrue(state.isBerulang)
    }

    @Test
    fun `ubah tanggal berakhir sebelum tanggal mulai - tanggal mulai otomatis disesuaikan`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        runCurrent()

        val initialStart = viewModel.state.value.tanggalMulai
        viewModel.onEvent(AddEditBudgetEvent.TanggalBerakhirChanged(initialStart))

        assertTrue(viewModel.state.value.tanggalMulai < viewModel.state.value.tanggalBerakhir)
    }

    @Test
    fun `dialog target tanggal - buka dan dismiss`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        runCurrent()

        viewModel.onEvent(AddEditBudgetEvent.TanggalMulaiClicked(viewModel.state.value.tanggalMulai))
        assertEquals(DateFieldTarget.TanggalMulai, viewModel.state.value.activeDateDialog)

        viewModel.onEvent(AddEditBudgetEvent.TanggalDismissClicked)
        assertNull(viewModel.state.value.activeDateDialog)
    }

    @Test
    fun `batal - mengirim efek NavigateBack`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val effects = mutableListOf<AddEditBudgetEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects += it } }
        runCurrent()

        viewModel.onEvent(AddEditBudgetEvent.BatalClicked)
        runCurrent()

        assertEquals(listOf(AddEditBudgetEffect.NavigateBack), effects)
    }

    @Test
    fun `hapus budget - memunculkan konfirmasi hapus dan menjalankan delete`() = runTest(testDispatcher) {
        budgetRepository.seed(
            testBudget(id = "b1", categoryId = "cat_1", amount = 500_000L),
        )

        val viewModel = createViewModel(budgetId = "b1", categoryId = "cat_1")
        val effects = mutableListOf<AddEditBudgetEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects += it } }
        runCurrent()

        viewModel.onEvent(AddEditBudgetEvent.HapusClicked)
        assertTrue(viewModel.state.value.showDeleteConfirmation)

        viewModel.onEvent(AddEditBudgetEvent.KonfirmasiHapusClicked)
        runCurrent()

        assertEquals(listOf("b1"), budgetRepository.deletedIds)
        assertEquals(listOf(AddEditBudgetEffect.NavigateBack), effects)
    }

    @Test
    fun `simpan anggaran - berhasil dan mengirim efek NavigateBack`() = runTest(testDispatcher) {
        categoryRepository.seed(
            testCategory(id = "cat_1", name = "Transportasi"),
        )

        val viewModel = createViewModel(categoryId = "cat_1")
        val effects = mutableListOf<AddEditBudgetEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects += it } }
        runCurrent()

        viewModel.onEvent(AddEditBudgetEvent.BatasBaruChanged("600000"))
        viewModel.onEvent(AddEditBudgetEvent.SimpanClicked)
        runCurrent()

        assertEquals(listOf(AddEditBudgetEffect.NavigateBack), effects)
        assertTrue(budgetRepository.stored.any { it.categoryId == "cat_1" && it.amount == 600_000L })
    }
}
