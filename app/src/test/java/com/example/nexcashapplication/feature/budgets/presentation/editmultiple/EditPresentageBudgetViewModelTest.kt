package com.example.nexcashapplication.feature.budgets.presentation.editmultiple

import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import com.example.nexcashapplication.feature.budgets.domain.usecase.DeleteBudgetUseCase
import com.example.nexcashapplication.feature.budgets.domain.usecase.FakeBudgetRepository
import com.example.nexcashapplication.feature.budgets.domain.usecase.GetBudgetPeriodUseCase
import com.example.nexcashapplication.feature.budgets.domain.usecase.SaveBudgetUseCase
import com.example.nexcashapplication.feature.category.presentation.main.FakeCategoryRepository
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import com.example.nexcashapplication.feature.transaction.domain.usecase.FakeTransactionRepository
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
class EditPresentageBudgetViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var categoryRepository: FakeCategoryRepository
    private lateinit var budgetRepository: FakeBudgetRepository
    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var saveBudgetUseCase: SaveBudgetUseCase
    private lateinit var deleteBudgetUseCase: DeleteBudgetUseCase

    @BeforeEach
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        categoryRepository = FakeCategoryRepository()
        budgetRepository = FakeBudgetRepository()
        transactionRepository = FakeTransactionRepository()
        val getBudgetPeriodUseCase = GetBudgetPeriodUseCase()
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

    private fun createViewModel() = EditPresentageBudgetViewModel(
        categoryRepository = categoryRepository,
        budgetRepository = budgetRepository,
        transactionRepository = transactionRepository,
        saveBudgetUseCase = saveBudgetUseCase,
        deleteBudgetUseCase = deleteBudgetUseCase,
    )

    @Test
    fun `load data - menghitung pot persentase dan akumulasi pengeluaran terpakai`() = runTest(testDispatcher) {
        val today = LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1)
        val endOfMonth = today.withDayOfMonth(today.lengthOfMonth())

        categoryRepository.seed(
            testCategory(id = "c1", name = "Makanan", color = "#FF0000"),
            testCategory(id = "c2", name = "Transport", color = "#00FF00"),
        )
        budgetRepository.seed(
            testBudget(id = "b1", categoryId = "c1", start = startOfMonth, end = endOfMonth, amount = 600_000L),
            testBudget(id = "b2", categoryId = "c2", start = startOfMonth, end = endOfMonth, amount = 400_000L),
        )
        transactionRepository.seed(
            TransactionWithDetails(
                Transaction(
                    id = "t1",
                    title = "Makan Siang",
                    amount = 150_000L,
                    categoryId = "c1",
                    description = null,
                    typeTransaction = TypeCategory.EXPENSE,
                    accountId = "a1",
                ),
            ),
        )

        val viewModel = createViewModel()
        runCurrent()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(1_000_000L, state.totalBudget)
        assertEquals(150_000L, state.totalUsed)
        assertEquals(850_000L, state.totalRemaining)
        assertEquals(2, state.budgets.size)

        val item1 = state.budgets.first { it.id == "c1" }
        assertEquals(60f, item1.percentage)
        assertEquals(600_000L, item1.totalBudget)

        val item2 = state.budgets.first { it.id == "c2" }
        assertEquals(40f, item2.percentage)
        assertEquals(400_000L, item2.totalBudget)
    }

    @Test
    fun `slider change - persentase berubah dan nominal dihitung proporsional terhadap pot`() = runTest(testDispatcher) {
        categoryRepository.seed(
            testCategory(id = "c1", name = "Makanan"),
            testCategory(id = "c2", name = "Transport"),
        )
        budgetRepository.seed(
            testBudget(id = "b1", categoryId = "c1", amount = 1_000_000L),
        )

        val viewModel = createViewModel()
        runCurrent()

        viewModel.onEvent(EditPresentageBudgetEvent.OnSliderChange(id = "c1", newPercent = 50))
        runCurrent()

        val updated = viewModel.state.value.budgets.first { it.id == "c1" }
        assertEquals(50f, updated.percentage)
        assertEquals(500_000L, updated.totalBudget)
    }

    @Test
    fun `amount input change - nominal berubah dan persentase dihitung ulang`() = runTest(testDispatcher) {
        categoryRepository.seed(
            testCategory(id = "c1", name = "Makanan"),
        )
        budgetRepository.seed(
            testBudget(id = "b1", categoryId = "c1", amount = 1_000_000L),
        )

        val viewModel = createViewModel()
        runCurrent()

        viewModel.onEvent(EditPresentageBudgetEvent.OnAmountInputChange(id = "c1", newAmount = 800_000L))
        runCurrent()

        val updated = viewModel.state.value.budgets.first { it.id == "c1" }
        assertEquals(800_000L, updated.totalBudget)
        assertEquals(80f, updated.percentage)
    }

    @Test
    fun `simpan gagal - alokasi kosong memunculkan snackbar error`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val effects = mutableListOf<EditPresentageBudgetEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects += it } }
        runCurrent()

        viewModel.onEvent(EditPresentageBudgetEvent.OnSaveClick)
        runCurrent()

        assertTrue(effects.any { it is EditPresentageBudgetEffect.ShowSnackbar })
    }

    @Test
    fun `simpan gagal - total persentase melebihi 100 persen memunculkan snackbar error`() = runTest(testDispatcher) {
        categoryRepository.seed(testCategory(id = "c1", name = "Makanan"))
        budgetRepository.seed(testBudget(id = "b1", categoryId = "c1", amount = 1_000_000L))

        val viewModel = createViewModel()
        val effects = mutableListOf<EditPresentageBudgetEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects += it } }
        runCurrent()

        // Ubah persentase menjadi 120% (> 100%)
        viewModel.onEvent(EditPresentageBudgetEvent.OnSliderChange(id = "c1", newPercent = 120))
        viewModel.onEvent(EditPresentageBudgetEvent.OnSaveClick)
        runCurrent()

        assertTrue(effects.any { it is EditPresentageBudgetEffect.ShowSnackbar })
    }

    @Test
    fun `simpan sukses - total tepat 100 persen mengirim efek NavigateBack`() = runTest(testDispatcher) {
        categoryRepository.seed(
            testCategory(id = "c1", name = "Makanan"),
        )
        budgetRepository.seed(
            testBudget(id = "b1", categoryId = "c1", amount = 1_000_000L),
        )

        val viewModel = createViewModel()
        val effects = mutableListOf<EditPresentageBudgetEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects += it } }
        runCurrent()

        // Simpan ketika 100%
        viewModel.onEvent(EditPresentageBudgetEvent.OnSaveClick)
        runCurrent()

        assertTrue(effects.any { it is EditPresentageBudgetEffect.NavigateBack })
    }

    @Test
    fun `dialog hapus alokasi kategori - buka dismiss dan konfirmasi`() = runTest(testDispatcher) {
        categoryRepository.seed(testCategory(id = "c1", name = "Makanan"))
        budgetRepository.seed(testBudget(id = "b1", categoryId = "c1", amount = 500_000L))

        val viewModel = createViewModel()
        val effects = mutableListOf<EditPresentageBudgetEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects += it } }
        runCurrent()

        viewModel.onEvent(EditPresentageBudgetEvent.OnDeleteBudgetClick("c1"))
        assertEquals("c1", viewModel.state.value.deleteConfirmationId)

        viewModel.onEvent(EditPresentageBudgetEvent.OnDismissDeleteClick)
        assertNull(viewModel.state.value.deleteConfirmationId)

        viewModel.onEvent(EditPresentageBudgetEvent.OnDeleteBudgetClick("c1"))
        viewModel.onEvent(EditPresentageBudgetEvent.OnConfirmDeleteClick)
        runCurrent()

        assertNull(viewModel.state.value.deleteConfirmationId)
        assertTrue(effects.any { it is EditPresentageBudgetEffect.ShowSnackbar })
    }

    @Test
    fun `navigasi - kembali dan tambah kategori`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val effects = mutableListOf<EditPresentageBudgetEffect>()
        backgroundScope.launch { viewModel.effect.collect { effects += it } }
        runCurrent()

        viewModel.onEvent(EditPresentageBudgetEvent.OnBackClick)
        viewModel.onEvent(EditPresentageBudgetEvent.OnAddCategoryClick)
        runCurrent()

        assertEquals(
            listOf(
                EditPresentageBudgetEffect.NavigateBack,
                EditPresentageBudgetEffect.NavigateToAddCategory,
            ),
            effects,
        )
    }
}
