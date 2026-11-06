package com.example.nexcashapplication.feature.account.domain.usecase

import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.account.domain.model.AccountModel
import com.example.nexcashapplication.feature.account.domain.model.DeleteAccountDesision
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import com.example.nexcashapplication.feature.transaction.domain.usecase.FakeAccountRepository
import com.example.nexcashapplication.feature.transaction.domain.usecase.FakeTransactionRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ValidationDeleteAccountUseCaseTest {

    private lateinit var accountRepository: FakeAccountRepository
    private lateinit var transactionRepository: FakeTransactionRepository
    private lateinit var useCase: ValidationDeleteAccountUseCase

    @BeforeEach
    fun setUp() {
        accountRepository = FakeAccountRepository()
        transactionRepository = FakeTransactionRepository()
        useCase = ValidationDeleteAccountUseCase(accountRepository, transactionRepository)
    }

    private fun withDetails(id: String, accountId: String) =
        TransactionWithDetails(
            transaction = Transaction(
                id = id,
                title = "tx-$id",
                amount = 10_000,
                categoryId = "c1",
                description = null,
                typeTransaction = TypeCategory.EXPENSE,
                accountId = accountId,
            ),
        )

    @Test
    fun `saldo tidak nol - pemblokiran hapus akun dengan saldo aktif`() = runTest {
        val account = AccountModel(
            id = "acc1",
            name = "BCA",
            currentBalance = 500_000L,
            accountType = TypeAccount.BANK,
        )
        accountRepository.seed(account)

        val decision = useCase("acc1").first()

        assertTrue(decision is DeleteAccountDesision.Blocked)
        assertEquals(500_000L, (decision as DeleteAccountDesision.Blocked).balance)
    }

    @Test
    fun `saldo nol dan ada transaksi - konfirmasi hapus dengan riwayat transaksi`() = runTest {
        val account = AccountModel(
            id = "acc1",
            name = "BCA",
            currentBalance = 0L,
            accountType = TypeAccount.BANK,
        )
        accountRepository.seed(account)
        transactionRepository.seed(
            withDetails("tx1", "acc1"),
            withDetails("tx2", "acc1"),
            withDetails("tx3", "other"),
        )

        val decision = useCase("acc1").first()

        assertTrue(decision is DeleteAccountDesision.ConfirmWithHistory)
        assertEquals(2, (decision as DeleteAccountDesision.ConfirmWithHistory).transactionCount)
    }

    @Test
    fun `saldo nol dan tanpa transaksi - konfirmasi hapus sederhana`() = runTest {
        val account = AccountModel(
            id = "acc1",
            name = "Cash",
            currentBalance = 0L,
            accountType = TypeAccount.CASH,
        )
        accountRepository.seed(account)

        val decision = useCase("acc1").first()

        assertEquals(DeleteAccountDesision.ConfirmSimple, decision)
    }
}
