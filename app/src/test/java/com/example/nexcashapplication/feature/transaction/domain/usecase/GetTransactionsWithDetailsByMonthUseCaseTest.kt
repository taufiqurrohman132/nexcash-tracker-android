package com.example.nexcashapplication.feature.transaction.domain.usecase

import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.Instant

class GetTransactionsWithDetailsByMonthUseCaseTest {

    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var useCase: GetTransactionsWithDetailsByMonthUseCase

    @BeforeEach
    fun setUp() {
        transactionRepository = FakeTransactionRepository()
        useCase = GetTransactionsWithDetailsByMonthUseCase(transactionRepository)
    }

    @Test
    fun `mengambil semua transaksi dengan detail bila categoryFilterId null`() = runTest {
        val cat1 = Category(id = "cat-1", name = "Makanan", type = TypeCategory.EXPENSE, iconId = "food", color = "#FF0000")
        val cat2 = Category(id = "cat-2", name = "Transport", type = TypeCategory.EXPENSE, iconId = "car", color = "#0000FF")

        val tx1 = Transaction(
            id = "tx-1",
            amount = 50_000L,
            title = "Makan Siang",
            categoryId = "cat-1",
            description = null,
            dateTime = Instant.fromEpochMilliseconds(1_700_000_000_000L),
            typeTransaction = TypeCategory.EXPENSE,
            accountId = "acc-1",
        )
        val tx2 = Transaction(
            id = "tx-2",
            amount = 30_000L,
            title = "Bensin",
            categoryId = "cat-2",
            description = null,
            dateTime = Instant.fromEpochMilliseconds(1_700_100_000_000L),
            typeTransaction = TypeCategory.EXPENSE,
            accountId = "acc-1",
        )

        transactionRepository.seed(
            TransactionWithDetails(transaction = tx1, category = cat1),
            TransactionWithDetails(transaction = tx2, category = cat2),
        )

        val result = useCase(yearMonth = "2026-11", categoryFilterId = null).first()

        assertEquals(2, result.size)
    }

    @Test
    fun `memfilter transaksi berdasarkan categoryFilterId jika diberikan`() = runTest {
        val cat1 = Category(id = "cat-1", name = "Makanan", type = TypeCategory.EXPENSE, iconId = "food", color = "#FF0000")
        val cat2 = Category(id = "cat-2", name = "Transport", type = TypeCategory.EXPENSE, iconId = "car", color = "#0000FF")

        val tx1 = Transaction(
            id = "tx-1",
            amount = 50_000L,
            title = "Makan Siang",
            categoryId = "cat-1",
            description = null,
            dateTime = Instant.fromEpochMilliseconds(1_700_000_000_000L),
            typeTransaction = TypeCategory.EXPENSE,
            accountId = "acc-1",
        )
        val tx2 = Transaction(
            id = "tx-2",
            amount = 30_000L,
            title = "Bensin",
            categoryId = "cat-2",
            description = null,
            dateTime = Instant.fromEpochMilliseconds(1_700_100_000_000L),
            typeTransaction = TypeCategory.EXPENSE,
            accountId = "acc-1",
        )

        transactionRepository.seed(
            TransactionWithDetails(transaction = tx1, category = cat1),
            TransactionWithDetails(transaction = tx2, category = cat2),
        )

        val result = useCase(yearMonth = "2026-11", categoryFilterId = "cat-1").first()

        assertEquals(1, result.size)
        assertEquals("tx-1", result[0].transaction.id)
        assertEquals("Makan Siang", result[0].transaction.title)
    }
}
