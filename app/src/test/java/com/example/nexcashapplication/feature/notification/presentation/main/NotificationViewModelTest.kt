package com.example.nexcashapplication.feature.notification.presentation.main

import com.example.nexcashapplication.awaitSettled
import com.example.nexcashapplication.core.common.getMonthlyPeriod
import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import com.example.nexcashapplication.feature.budgets.domain.usecase.FakeBudgetRepository
import com.example.nexcashapplication.feature.budgets.domain.usecase.GetBudgetPeriodUseCase
import com.example.nexcashapplication.feature.category.presentation.main.FakeCategoryRepository
import com.example.nexcashapplication.feature.category.presentation.main.FakeTransactionRepository
import com.example.nexcashapplication.feature.notification.FakeNotificationPreferenceRepository
import com.example.nexcashapplication.feature.notification.domain.usecase.GetBudgetAlertsUseCase
import com.example.nexcashapplication.feature.settings.presentation.currency.FakeSettingsRepository
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var categoryRepository: FakeCategoryRepository
    private lateinit var budgetRepository: FakeBudgetRepository
    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var notificationPreferenceRepository: FakeNotificationPreferenceRepository
    private lateinit var settingsRepository: FakeSettingsRepository
    private lateinit var getBudgetAlertsUseCase: GetBudgetAlertsUseCase

    private val today = LocalDate.now()
    private lateinit var monthStart: LocalDate
    private lateinit var monthEnd: LocalDate

    private val category = Category(
        id = "cat-food",
        parentId = null,
        name = "Makanan",
        type = TypeCategory.EXPENSE,
        iconId = "food",
        color = "#FF5722",
    )

    @BeforeEach
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        val period = getMonthlyPeriod(today)
        monthStart = period.first
        monthEnd = period.second

        categoryRepository = FakeCategoryRepository()
        budgetRepository = FakeBudgetRepository()
        transactionRepository = FakeTransactionRepository()
        notificationPreferenceRepository = FakeNotificationPreferenceRepository()
        settingsRepository = FakeSettingsRepository()

        getBudgetAlertsUseCase = GetBudgetAlertsUseCase(
            categoryRepository = categoryRepository,
            budgetRepository = budgetRepository,
            transactionRepository = transactionRepository,
            getBudgetPeriodUseCase = GetBudgetPeriodUseCase(),
            notificationPreferenceRepository = notificationPreferenceRepository,
            settingsRepository = settingsRepository,
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load - memuat alert dan mengelompokkan ke grup hari ini dengan unread count yang benar`() =
        runTest(testDispatcher) {
            categoryRepository.seed(category)
            budgetRepository.seed(
                Budget(
                    id = "b1",
                    categoryId = category.id,
                    startDate = monthStart,
                    endDate = monthEnd,
                    amount = 1_000_000L,
                    recurrenceType = RecurrenceType.MONTHLY,
                    isRecurring = true,
                ),
            )
            // 85% pemakaian -> menghasilkan 1 alert (threshold 80)
            seedExpense(850_000L)

            val vm = createViewModel()
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            val state = vm.state.value
            assertFalse(state.isLoading)
            assertEquals(1, state.unreadCount)
            assertEquals(1, state.groups.size)
            assertEquals(1, state.groups.first().items.size)
            assertEquals("b1-80", state.groups.first().items.first().id)
            assertFalse(state.groups.first().items.first().isRead)
        }

    @Test
    fun `ItemClicked - menandai alert sebagai dibaca dan mengirim effect NavigateToBudget`() =
        runTest(testDispatcher) {
            categoryRepository.seed(category)
            budgetRepository.seed(
                Budget(
                    id = "b1",
                    categoryId = category.id,
                    startDate = monthStart,
                    endDate = monthEnd,
                    amount = 1_000_000L,
                    recurrenceType = RecurrenceType.MONTHLY,
                    isRecurring = true,
                ),
            )
            seedExpense(850_000L)

            val vm = createViewModel()
            val effects = collectEffects(vm)
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            assertEquals(1, vm.state.value.unreadCount)

            vm.onEvent(NotificationEvent.ItemClicked(id = "b1-80", budgetId = "b1"))
            advanceUntilIdle()

            assertEquals(0, vm.state.value.unreadCount)
            assertTrue(vm.state.value.groups.first().items.first().isRead)
            assertEquals(listOf(NotificationEffect.NavigateToBudget("b1")), effects)
        }

    @Test
    fun `MarkAllReadClicked - menandai semua alert terbaca sehingga unreadCount 0`() =
        runTest(testDispatcher) {
            categoryRepository.seed(category)
            budgetRepository.seed(
                Budget(
                    id = "b1",
                    categoryId = category.id,
                    startDate = monthStart,
                    endDate = monthEnd,
                    amount = 1_000_000L,
                    recurrenceType = RecurrenceType.MONTHLY,
                    isRecurring = true,
                ),
            )
            // 100% -> menghasilkan 2 alert (threshold 100 dan threshold 80)
            seedExpense(1_000_000L)

            val vm = createViewModel()
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            assertEquals(2, vm.state.value.unreadCount)

            vm.onEvent(NotificationEvent.MarkAllReadClicked)
            awaitSettled(state = { vm.state.value }, isLoading = { it.unreadCount > 0 })

            assertEquals(0, vm.state.value.unreadCount)
            assertTrue(vm.state.value.groups.first().items.all { it.isRead })
        }

    @Test
    fun `BackClicked dan SettingsClicked - mengirim effect navigasi yang sesuai`() =
        runTest(testDispatcher) {
            val vm = createViewModel()
            val effects = collectEffects(vm)
            advanceUntilIdle()

            vm.onEvent(NotificationEvent.BackClicked)
            vm.onEvent(NotificationEvent.SettingsClicked)
            advanceUntilIdle()

            assertEquals(
                listOf(
                    NotificationEffect.NavigateBack,
                    NotificationEffect.NavigateToSettings,
                ),
                effects,
            )
        }

    @Test
    fun `error - kegagalan pembacaan menghentikan loading dan mengisi pesan error`() =
        runTest(testDispatcher) {
            categoryRepository.throwOnLoad = RuntimeException("Database error")

            val vm = createViewModel()
            awaitSettled(state = { vm.state.value }, isLoading = { it.isLoading })

            assertFalse(vm.state.value.isLoading)
            assertNotNull(vm.state.value.error)
            val error = vm.state.value.error
            assertTrue(
                error is UiText.Raw && error.value.contains("Database error"),
                "Pesan error terbawa: $error",
            )
        }

    private fun createViewModel() = NotificationViewModel(
        getBudgetAlertsUseCase = getBudgetAlertsUseCase,
    )

    private fun TestScope.collectEffects(vm: NotificationViewModel): MutableList<NotificationEffect> {
        val effects = mutableListOf<NotificationEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.effect.collect { effects += it }
        }
        return effects
    }

    private fun seedExpense(amount: Long) {
        transactionRepository.seed(
            Transaction(
                id = "tx-1",
                title = "Expense",
                amount = amount,
                categoryId = category.id,
                description = null,
                dateTime = Instant.fromEpochMilliseconds(System.currentTimeMillis()),
                typeTransaction = TypeCategory.EXPENSE,
                accountId = "acc-1",
            ),
        )
    }
}
