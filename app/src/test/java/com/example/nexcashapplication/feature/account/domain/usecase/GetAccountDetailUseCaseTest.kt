package com.example.nexcashapplication.feature.account.domain.usecase

import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import com.example.nexcashapplication.feature.transaction.domain.usecase.FakeAccountRepository
import com.example.nexcashapplication.feature.transaction.domain.usecase.FakeTransactionRepository
import com.example.nexcashapplication.feature.transaction.domain.usecase.sampleBankAccount
import com.example.nexcashapplication.core.domain.model.TypeCategory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Unit test [GetAccountDetailUseCase] — "transaksi terakhir" di detail akun:
 * - hanya transaksi milik akun yang dipanggil (dulu: filter kategori `""` yang
 *   tidak pernah cocok → daftar selalu kosong)
 * - dibatasi [GetAccountDetailUseCase.RECENT_LIMIT] entri terbaru
 */
class GetAccountDetailUseCaseTest {

    private lateinit var accountRepository: FakeAccountRepository
    private lateinit var transactionRepository: FakeTransactionRepository

    @BeforeEach
    fun setUp() {
        accountRepository = FakeAccountRepository()
        transactionRepository = FakeTransactionRepository()
        accountRepository.seed(sampleBankAccount(id = "bca", name = "BCA"))
    }

    @Test
    fun `transaksi terakhir - hanya transaksi milik akun yang dipanggil`() = runTest {
        transactionRepository.seed(
            withDetails("t1", accountId = "bca"),
            withDetails("t2", accountId = "bca"),
            withDetails("t3", accountId = "gopay"),
        )

        val data = GetAccountDetailUseCase(accountRepository, transactionRepository)("bca").first()

        // t3 (akun lain) tidak ikut — dulu daftar ini selalu kosong
        assertEquals(listOf("t1", "t2"), data.recentTransactions.map { it.transaction.id })
        assertEquals("bca", data.account.id)
    }

    @Test
    fun `transaksi terakhir - dibatasi 6 entri`() = runTest {
        for (n in 1..8) {
            transactionRepository.seed(withDetails("t$n", accountId = "bca"))
        }

        val data = GetAccountDetailUseCase(accountRepository, transactionRepository)("bca").first()

        assertEquals(6, data.recentTransactions.size)
        assertEquals(
            listOf("t1", "t2", "t3", "t4", "t5", "t6"),
            data.recentTransactions.map { it.transaction.id },
        )
    }

    private fun withDetails(id: String, accountId: String) =
        TransactionWithDetails(
            transaction = Transaction(
                id = id,
                title = "tx-$id",
                amount = 10_000,
                categoryId = "c1",
                description = null,
                dateTime = Instant.fromEpochMilliseconds(
                    Clock.System.now().toEpochMilliseconds(),
                ),
                typeTransaction = TypeCategory.EXPENSE,
                accountId = accountId,
            ),
        )
}
