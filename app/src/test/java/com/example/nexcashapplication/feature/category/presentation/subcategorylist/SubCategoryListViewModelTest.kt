package com.example.nexcashapplication.feature.category.presentation.subcategorylist

import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import com.example.nexcashapplication.feature.budgets.domain.usecase.FakeBudgetRepository
import com.example.nexcashapplication.feature.budgets.domain.usecase.GetBudgetPeriodUseCase
import com.example.nexcashapplication.feature.category.domain.usecase.GetSubCategoriesWithBudgetUseCase
import com.example.nexcashapplication.feature.category.presentation.main.FakeCategoryRepository
import com.example.nexcashapplication.feature.category.presentation.main.FakeTransactionRepository
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
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
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.time.Clock

@OptIn(ExperimentalCoroutinesApi::class)
class SubCategoryListViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var categoryRepository: FakeCategoryRepository
    private lateinit var budgetRepository: FakeBudgetRepository
    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var getBudgetPeriodUseCase: GetBudgetPeriodUseCase
    private lateinit var getSubCategoriesWithBudgetUseCase: GetSubCategoriesWithBudgetUseCase

    private val parentId = "parent-food"
    private val parentName = "Makanan"
    private val today = LocalDate.now()

    @BeforeEach
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        categoryRepository = FakeCategoryRepository()
        budgetRepository = FakeBudgetRepository()
        transactionRepository = FakeTransactionRepository()
        getBudgetPeriodUseCase = GetBudgetPeriodUseCase()

        getSubCategoriesWithBudgetUseCase = GetSubCategoriesWithBudgetUseCase(
            categoryRepository = categoryRepository,
            budgetRepository = budgetRepository,
            transactionRepository = transactionRepository,
            getBudgetPeriodUseCase = getBudgetPeriodUseCase,
        )

        categoryRepository.seed(
            Category(id = parentId, name = parentName, type = TypeCategory.EXPENSE, color = "#FF0000", iconId = "food"),
            Category(id = "sub-1", parentId = parentId, name = "Restoran", type = TypeCategory.EXPENSE, color = "#00FF00", iconId = "res"),
            Category(id = "sub-2", parentId = parentId, name = "Snack", type = TypeCategory.EXPENSE, color = "#0000FF", iconId = "snack"),
        )

        budgetRepository.seed(
            Budget(
                id = "b-parent",
                categoryId = parentId,
                amount = 2_000_000L,
                isRecurring = false,
                recurrenceType = RecurrenceType.CUSTOM,
                startDate = today.minusDays(1),
                endDate = today.plusDays(1),
            ),
            Budget(
                id = "b-sub1",
                categoryId = "sub-1",
                amount = 500_000L,
                isRecurring = false,
                recurrenceType = RecurrenceType.CUSTOM,
                startDate = today.minusDays(1),
                endDate = today.plusDays(1),
            ),
        )

        val now = Clock.System.now()
        transactionRepository.seed(
            Transaction(
                id = "t1",
                title = "tx-sub-1",
                description = null,
                accountId = "acc-1",
                categoryId = "sub-1",
                typeTransaction = TypeCategory.EXPENSE,
                amount = 150_000L,
                dateTime = now,
            ),
            Transaction(
                id = "t2",
                title = "tx-sub-2",
                description = null,
                accountId = "acc-1",
                categoryId = "sub-2",
                typeTransaction = TypeCategory.EXPENSE,
                amount = 50_000L,
                dateTime = now,
            ),
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): SubCategoryListViewModel {
        return SubCategoryListViewModel(
            route = Route.CategoryRoute.SubCategoryList(categoryId = parentId, categoryName = parentName),
            getSubCategoriesWithBudgetUseCase = getSubCategoriesWithBudgetUseCase,
        )
    }

    @Test
    fun `observerSubCategories memuat daftar subkategori lengkap dengan budget dan terpakai`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            val state = vm.state.value
            assertFalse(state.isLoading)
            assertNull(state.error)
            assertEquals(parentName, state.parentCategoryName)
            assertEquals(2_000_000L, state.parentTotalBudget)
            assertEquals(2, state.subCategories.size)

            val sub1 = state.subCategories.find { it.id == "sub-1" }!!
            assertEquals("Restoran", sub1.name)
            assertEquals(500_000L, sub1.totalBudget)
            assertEquals(150_000L, sub1.usedBudget)

            val sub2 = state.subCategories.find { it.id == "sub-2" }!!
            assertEquals("Snack", sub2.name)
            assertEquals(0L, sub2.totalBudget)
            assertEquals(50_000L, sub2.usedBudget)
        }

    @Test
    fun `event OnBackClick mengirim efek NavigateBack`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            val effects = mutableListOf<SubCategoryListEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                vm.effect.collect { effects += it }
            }

            vm.onEvent(SubCategoryListEvent.OnBackClick)
            advanceUntilIdle()

            assertEquals(listOf(SubCategoryListEffect.NavigateBack), effects)
        }

    @Test
    fun `event OnSubCategoryClick mengirim efek NavigateToSubCategoryDetail`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            val effects = mutableListOf<SubCategoryListEffect>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                vm.effect.collect { effects += it }
            }

            vm.onEvent(SubCategoryListEvent.OnSubCategoryClick("sub-1"))
            advanceUntilIdle()

            assertEquals(listOf(SubCategoryListEffect.NavigateToSubCategoryDetail("sub-1")), effects)
        }

    @Test
    fun `event OnRetryClick memuat ulang data ketika terjadi error`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            advanceUntilIdle()

            vm.onEvent(SubCategoryListEvent.OnRetryClick)
            advanceUntilIdle()

            val state = vm.state.value
            assertFalse(state.isLoading)
            assertNull(state.error)
            assertEquals(2, state.subCategories.size)
        }
}
