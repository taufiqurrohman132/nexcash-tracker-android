package com.example.nexcashapplication.feature.transaction.domain.usecase

import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.Instant

class GetTransactionsByMonthUseCaseTest {

    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var useCase: GetTransactionsByMonthUseCase

    @BeforeEach
    fun setUp() {
        transactionRepository = FakeTransactionRepository()
        useCase = GetTransactionsByMonthUseCase(transactionRepository)
    }

    @Test
    fun `mengambil daftar transaksi bulanan dari repository`() = runTest {
        val tx1 = Transaction(
            id = "tx-1",
            amount = 50_000L,
            title = "Bensin",
            categoryId = "cat-1",
            description = null,
            dateTime = Instant.fromEpochMilliseconds(1_700_000_000_000L),
            typeTransaction = TypeCategory.EXPENSE,
            accountId = "acc-1",
        )
        val tx2 = Transaction(
            id = "tx-2",
            amount = 100_000L,
            title = "Makan Siang",
            categoryId = "cat-1",
            description = null,
            dateTime = Instant.fromEpochMilliseconds(1_700_100_000_000L),
            typeTransaction = TypeCategory.EXPENSE,
            accountId = "acc-1",
        )
        transactionRepository.seed(
            TransactionWithDetails(transaction = tx1),
            TransactionWithDetails(transaction = tx2),
        )

        val result = useCase("2026-11").first()

        assertEquals(2, result.size)
        assertEquals("Bensin", result[0].title)
        assertEquals("Makan Siang", result[1].title)
    }
}
