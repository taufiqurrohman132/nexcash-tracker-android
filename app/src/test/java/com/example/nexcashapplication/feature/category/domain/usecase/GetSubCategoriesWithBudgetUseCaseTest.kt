package com.example.nexcashapplication.feature.category.domain.usecase

import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import com.example.nexcashapplication.feature.budgets.domain.usecase.FakeBudgetRepository
import com.example.nexcashapplication.feature.budgets.domain.usecase.GetBudgetPeriodUseCase
import com.example.nexcashapplication.feature.category.presentation.main.FakeCategoryRepository
import com.example.nexcashapplication.feature.category.presentation.main.FakeTransactionRepository
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

class GetSubCategoriesWithBudgetUseCaseTest {

    private lateinit var categoryRepository: FakeCategoryRepository
    private lateinit var budgetRepository: FakeBudgetRepository
    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var getBudgetPeriodUseCase: GetBudgetPeriodUseCase
    private lateinit var useCase: GetSubCategoriesWithBudgetUseCase

    private val today: LocalDate = LocalDate.now()

    @BeforeEach
    fun setUp() {
        categoryRepository = FakeCategoryRepository()
        budgetRepository = FakeBudgetRepository()
        transactionRepository = FakeTransactionRepository()
        getBudgetPeriodUseCase = GetBudgetPeriodUseCase()

        useCase = GetSubCategoriesWithBudgetUseCase(
            categoryRepository = categoryRepository,
            budgetRepository = budgetRepository,
            transactionRepository = transactionRepository,
            getBudgetPeriodUseCase = getBudgetPeriodUseCase,
        )
    }

    @Test
    fun `ketika subkategori kosong - mengembalikan hasil 0 dan daftar kosong`() = runTest {
        val parentId = "parent-1"
        categoryRepository.seed(
            category(parentId, "Makanan"),
        )

        val result = useCase(parentId).first()

        assertEquals(0L, result.parentBudgetAmount)
        assertEquals(0L, result.parentUsedAmount)
        assertTrue(result.subCategories.isEmpty())
    }

    @Test
    fun `subkategori tanpa budget sendiri dan induk punya budget - menghitung rollup dan terpakai`() = runTest {
        val parentId = "p1"
        val sub1Id = "s1"
        val sub2Id = "s2"

        val parentCat = category(parentId, "Makanan")
        val sub1 = category(sub1Id, "Makan Siang", parentId = parentId)
        val sub2 = category(sub2Id, "Camilan", parentId = parentId)

        categoryRepository.seed(parentCat, sub1, sub2)

        // Budget induk 1.000.000
        budgetRepository.seed(
            budget("b-parent", parentId, 1_000_000L),
        )

        // Transaksi induk 100k, sub1 50k, sub2 30k
        val now = Clock.System.now()
        transactionRepository.seed(
            transaction(parentId, 100_000L, now),
            transaction(sub1Id, 50_000L, now),
            transaction(sub2Id, 30_000L, now),
        )

        val result = useCase(parentId).first()

        assertEquals(1_000_000L, result.parentBudgetAmount)
        // Rollup induk = 100k + 50k + 30k = 180k
        assertEquals(180_000L, result.parentUsedAmount)
        assertEquals(2, result.subCategories.size)

        val sub1Detail = result.subCategories.find { it.category.id == sub1Id }!!
        assertEquals(0L, sub1Detail.totalBudget)
        assertEquals(50_000L, sub1Detail.usedBudget)

        val sub2Detail = result.subCategories.find { it.category.id == sub2Id }!!
        assertEquals(0L, sub2Detail.totalBudget)
        assertEquals(30_000L, sub2Detail.usedBudget)
    }

    @Test
    fun `subkategori dengan budget sendiri - terpakai dihitung pada periodenya sendiri`() = runTest {
        val parentId = "p1"
        val sub1Id = "s1"

        val parentCat = category(parentId, "Makanan")
        val sub1 = category(sub1Id, "Makan Siang", parentId = parentId)

        categoryRepository.seed(parentCat, sub1)

        budgetRepository.seed(
            budget("b-parent", parentId, 1_000_000L),
            budget("b-sub1", sub1Id, 400_000L),
        )

        val now = Clock.System.now()
        transactionRepository.seed(
            transaction(parentId, 100_000L, now),
            transaction(sub1Id, 75_000L, now),
        )

        val result = useCase(parentId).first()

        assertEquals(1_000_000L, result.parentBudgetAmount)
        assertEquals(175_000L, result.parentUsedAmount)

        val sub1Detail = result.subCategories.first()
        assertEquals(400_000L, sub1Detail.totalBudget)
        assertEquals(75_000L, sub1Detail.usedBudget)
    }

    @Test
    fun `induk tanpa budget - parentUsedAmount bernilai 0`() = runTest {
        val parentId = "p1"
        val sub1Id = "s1"

        categoryRepository.seed(
            category(parentId, "Makanan"),
            category(sub1Id, "Makan Siang", parentId = parentId),
        )

        val now = Clock.System.now()
        transactionRepository.seed(
            transaction(sub1Id, 25_000L, now),
        )

        val result = useCase(parentId).first()

        assertEquals(0L, result.parentBudgetAmount)
        assertEquals(0L, result.parentUsedAmount) // Tanpa budget induk, rollup = 0

        val sub1Detail = result.subCategories.first()
        assertEquals(0L, sub1Detail.totalBudget)
        assertEquals(25_000L, sub1Detail.usedBudget)
    }

    private fun category(id: String, name: String, parentId: String? = null) = Category(
        id = id,
        parentId = parentId,
        name = name,
        type = TypeCategory.EXPENSE,
        color = "#123456",
        iconId = "food",
    )

    private fun budget(id: String, categoryId: String, amount: Long) = Budget(
        id = id,
        categoryId = categoryId,
        amount = amount,
        isRecurring = false,
        recurrenceType = RecurrenceType.CUSTOM,
        startDate = today.minusDays(2),
        endDate = today.plusDays(2),
    )

    private fun transaction(categoryId: String, amount: Long, time: kotlin.time.Instant) = Transaction(
        id = java.util.UUID.randomUUID().toString(),
        title = "tx-$categoryId",
        description = null,
        accountId = "acc-1",
        categoryId = categoryId,
        typeTransaction = TypeCategory.EXPENSE,
        amount = amount,
        dateTime = time,
    )
}
