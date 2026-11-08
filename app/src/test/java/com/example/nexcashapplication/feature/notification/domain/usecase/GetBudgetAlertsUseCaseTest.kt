package com.example.nexcashapplication.feature.notification.domain.usecase

import com.example.nexcashapplication.core.common.getMonthlyPeriod
import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import com.example.nexcashapplication.feature.budgets.domain.usecase.FakeBudgetRepository
import com.example.nexcashapplication.feature.budgets.domain.usecase.GetBudgetPeriodUseCase
import com.example.nexcashapplication.feature.category.presentation.main.FakeCategoryRepository
import com.example.nexcashapplication.feature.category.presentation.main.FakeTransactionRepository
import com.example.nexcashapplication.feature.notification.FakeNotificationPreferenceRepository
import com.example.nexcashapplication.feature.notification.domain.model.BudgetAlertPreference
import com.example.nexcashapplication.feature.settings.presentation.currency.FakeSettingsRepository
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZoneId
import kotlin.time.Instant

class GetBudgetAlertsUseCaseTest {

    private lateinit var categoryRepository: FakeCategoryRepository
    private lateinit var budgetRepository: FakeBudgetRepository
    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var getBudgetPeriodUseCase: GetBudgetPeriodUseCase
    private lateinit var notificationPreferenceRepository: FakeNotificationPreferenceRepository
    private lateinit var settingsRepository: FakeSettingsRepository
    private lateinit var useCase: GetBudgetAlertsUseCase

    private val today: LocalDate = LocalDate.now()
    private lateinit var monthStart: LocalDate
    private lateinit var monthEnd: LocalDate

    private val foodCategory = Category(
        id = "cat-food",
        parentId = null,
        name = "Makanan",
        type = TypeCategory.EXPENSE,
        iconId = "food",
        color = "#FF5722",
    )

    private val snackSubCategory = Category(
        id = "cat-snack",
        parentId = "cat-food",
        name = "Camilan",
        type = TypeCategory.EXPENSE,
        iconId = "snack",
        color = "#FF9800",
    )

    @BeforeEach
    fun setUp() {
        val period = getMonthlyPeriod(today)
        monthStart = period.first
        monthEnd = period.second

        categoryRepository = FakeCategoryRepository()
        budgetRepository = FakeBudgetRepository()
        transactionRepository = FakeTransactionRepository()
        getBudgetPeriodUseCase = GetBudgetPeriodUseCase()
        notificationPreferenceRepository = FakeNotificationPreferenceRepository()
        settingsRepository = FakeSettingsRepository()

        useCase = GetBudgetAlertsUseCase(
            categoryRepository = categoryRepository,
            budgetRepository = budgetRepository,
            transactionRepository = transactionRepository,
            getBudgetPeriodUseCase = getBudgetPeriodUseCase,
            notificationPreferenceRepository = notificationPreferenceRepository,
            settingsRepository = settingsRepository,
        )
    }

    @Test
    fun `saklar master mati - tidak menghasilkan alert meskipun ada budget melewati threshold`() = runTest {
        settingsRepository.updateNotificationEnabled(false)
        categoryRepository.seed(foodCategory)

        budgetRepository.seed(
            Budget(
                id = "b1",
                categoryId = foodCategory.id,
                startDate = monthStart,
                endDate = monthEnd,
                amount = 1_000_000L,
                recurrenceType = RecurrenceType.MONTHLY,
                isRecurring = true,
            ),
        )

        // 900.000 dari 1.000.000 (90%)
        seedExpense(categoryId = foodCategory.id, amount = 900_000L)

        val alerts = useCase().first()
        assertTrue(alerts.isEmpty())
    }

    @Test
    fun `budget di bawah 80 persen - tidak menghasilkan alert`() = runTest {
        categoryRepository.seed(foodCategory)
        budgetRepository.seed(
            Budget(
                id = "b1",
                categoryId = foodCategory.id,
                startDate = monthStart,
                endDate = monthEnd,
                amount = 1_000_000L,
                recurrenceType = RecurrenceType.MONTHLY,
                isRecurring = true,
            ),
        )

        // 750.000 (75%)
        seedExpense(categoryId = foodCategory.id, amount = 750_000L)

        val alerts = useCase().first()
        assertTrue(alerts.isEmpty())
    }

    @Test
    fun `budget terpakai 85 persen - menghasilkan alert threshold 80`() = runTest {
        categoryRepository.seed(foodCategory)
        budgetRepository.seed(
            Budget(
                id = "b1",
                categoryId = foodCategory.id,
                startDate = monthStart,
                endDate = monthEnd,
                amount = 1_000_000L,
                recurrenceType = RecurrenceType.MONTHLY,
                isRecurring = true,
            ),
        )

        seedExpense(categoryId = foodCategory.id, amount = 850_000L)

        val alerts = useCase().first()
        assertEquals(1, alerts.size)
        val alert = alerts.first()
        assertEquals("b1-80", alert.id)
        assertEquals("b1", alert.budgetId)
        assertEquals("Makanan", alert.categoryName)
        assertEquals(85, alert.percentUsed)
        assertEquals(80, alert.threshold)
    }

    @Test
    fun `budget terpakai 100 persen atau lebih - menghasilkan 2 alert (100 dan 80)`() = runTest {
        categoryRepository.seed(foodCategory)
        budgetRepository.seed(
            Budget(
                id = "b1",
                categoryId = foodCategory.id,
                startDate = monthStart,
                endDate = monthEnd,
                amount = 1_000_000L,
                recurrenceType = RecurrenceType.MONTHLY,
                isRecurring = true,
            ),
        )

        seedExpense(categoryId = foodCategory.id, amount = 1_200_000L)

        val alerts = useCase().first()
        assertEquals(2, alerts.size)
        assertEquals(listOf(100, 80), alerts.map { it.threshold })
        assertEquals(100, alerts.first().percentUsed) // mentok di 100
    }

    @Test
    fun `rollup subkategori - pengeluaran di subkategori dihitung ke budget kategori induk`() = runTest {
        categoryRepository.seed(foodCategory, snackSubCategory)
        budgetRepository.seed(
            Budget(
                id = "b1",
                categoryId = foodCategory.id,
                startDate = monthStart,
                endDate = monthEnd,
                amount = 1_000_000L,
                recurrenceType = RecurrenceType.MONTHLY,
                isRecurring = true,
            ),
        )

        // 500.000 di induk + 350.000 di anak = 850.000 (85%)
        seedExpense(categoryId = foodCategory.id, amount = 500_000L)
        seedExpense(categoryId = snackSubCategory.id, amount = 350_000L)

        val alerts = useCase().first()
        assertEquals(1, alerts.size)
        assertEquals(85, alerts.first().percentUsed)
    }

    @Test
    fun `preferensi budget disabled - alert diabaikan untuk budget tersebut`() = runTest {
        categoryRepository.seed(foodCategory)
        budgetRepository.seed(
            Budget(
                id = "b1",
                categoryId = foodCategory.id,
                startDate = monthStart,
                endDate = monthEnd,
                amount = 1_000_000L,
                recurrenceType = RecurrenceType.MONTHLY,
                isRecurring = true,
            ),
        )
        notificationPreferenceRepository.seed(
            BudgetAlertPreference(budgetId = "b1", enabled = false),
        )

        seedExpense(categoryId = foodCategory.id, amount = 900_000L)

        val alerts = useCase().first()
        assertTrue(alerts.isEmpty())
    }

    @Test
    fun `preferensi threshold - hanya threshold yang diaktifkan yang muncul`() = runTest {
        categoryRepository.seed(foodCategory)
        budgetRepository.seed(
            Budget(
                id = "b1",
                categoryId = foodCategory.id,
                startDate = monthStart,
                endDate = monthEnd,
                amount = 1_000_000L,
                recurrenceType = RecurrenceType.MONTHLY,
                isRecurring = true,
            ),
        )
        // Hanya ingin notifikasi ketika 100%, 80% dimatikan
        notificationPreferenceRepository.seed(
            BudgetAlertPreference(budgetId = "b1", enabled = true, thresholds = setOf(100)),
        )

        seedExpense(categoryId = foodCategory.id, amount = 1_000_000L)

        val alerts = useCase().first()
        assertEquals(1, alerts.size)
        assertEquals(100, alerts.first().threshold)
    }

    @Test
    fun `transaksi di luar rentang tanggal budget - tidak dihitung ke pemakaian`() = runTest {
        categoryRepository.seed(foodCategory)
        budgetRepository.seed(
            Budget(
                id = "b1",
                categoryId = foodCategory.id,
                startDate = monthStart,
                endDate = monthEnd,
                amount = 1_000_000L,
                recurrenceType = RecurrenceType.MONTHLY,
                isRecurring = true,
            ),
        )

        // Transaksi bulan lalu
        val lastMonthMillis = monthStart.minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val txOld = Transaction(
            id = "tx-old",
            title = "Makan Bulan Lalu",
            amount = 900_000L,
            categoryId = foodCategory.id,
            description = null,
            dateTime = Instant.fromEpochMilliseconds(lastMonthMillis),
            typeTransaction = TypeCategory.EXPENSE,
            accountId = "acc-1",
        )
        transactionRepository.seed(txOld)

        val alerts = useCase().first()
        assertTrue(alerts.isEmpty())
    }

    private fun seedExpense(categoryId: String, amount: Long) {
        val tx = Transaction(
            id = "tx-${System.nanoTime()}",
            title = "Test Expense",
            amount = amount,
            categoryId = categoryId,
            description = null,
            dateTime = Instant.fromEpochMilliseconds(System.currentTimeMillis()),
            typeTransaction = TypeCategory.EXPENSE,
            accountId = "acc-1",
        )
        transactionRepository.seed(tx)
    }
}
