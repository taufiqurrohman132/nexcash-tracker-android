package com.example.nexcashapplication.feature.notification.domain.usecase

import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.category.presentation.main.FakeTransactionRepository
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZoneId
import kotlin.time.Instant

class HasTransactionTodayUseCaseTest {

    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var useCase: HasTransactionTodayUseCase

    @BeforeEach
    fun setUp() {
        transactionRepository = FakeTransactionRepository()
        useCase = HasTransactionTodayUseCase(transactionRepository)
    }

    @Test
    fun `mengembalikan false jika belum ada transaksi hari ini`() = runTest {
        val hasTransaction = useCase()
        assertFalse(hasTransaction)
    }

    @Test
    fun `mengembalikan true jika ada transaksi pada hari ini`() = runTest {
        val now = System.currentTimeMillis()
        val tx = Transaction(
            id = "tx-1",
            title = "Makan Siang",
            amount = 35_000,
            categoryId = "cat-food",
            description = null,
            dateTime = Instant.fromEpochMilliseconds(now),
            typeTransaction = TypeCategory.EXPENSE,
            accountId = "acc-1",
        )
        transactionRepository.seed(tx)

        val hasTransaction = useCase(now = now)
        assertTrue(hasTransaction)
    }

    @Test
    fun `mengembalikan false jika transaksi terjadi kemarin (di luar rentang hari ini)`() = runTest {
        val now = System.currentTimeMillis()
        val today = java.time.Instant.ofEpochMilli(now).atZone(ZoneId.systemDefault()).toLocalDate()
        val yesterdayMillis = today.minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() + 3600_000

        val tx = Transaction(
            id = "tx-yesterday",
            title = "Belanja Kemarin",
            amount = 50_000,
            categoryId = "cat-shopping",
            description = null,
            dateTime = Instant.fromEpochMilliseconds(yesterdayMillis),
            typeTransaction = TypeCategory.EXPENSE,
            accountId = "acc-1",
        )
        transactionRepository.seed(tx)

        val hasTransaction = useCase(now = now)
        assertFalse(hasTransaction)
    }
}
