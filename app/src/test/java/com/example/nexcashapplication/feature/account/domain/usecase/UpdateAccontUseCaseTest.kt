package com.example.nexcashapplication.feature.account.domain.usecase

import com.example.nexcashapplication.feature.account.domain.model.AccountModel
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.transaction.domain.usecase.FakeAccountRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateAccontUseCaseTest {

    private lateinit var accountRepository: FakeAccountRepository
    private lateinit var useCase: UpdateAccontUseCase

    @BeforeEach
    fun setUp() {
        accountRepository = FakeAccountRepository()
        useCase = UpdateAccontUseCase(accountRepository)
    }

    @Test
    fun `perbarui akun berhasil - mengembalikan Result success dan terupdate di repository`() = runTest {
        val initialAccount = AccountModel(id = "acc1", name = "BCA Lama", accountType = TypeAccount.BANK)
        accountRepository.seed(initialAccount)

        val updatedAccount = initialAccount.copy(name = "BCA Baru", currentBalance = 1_000_000L)
        val result = useCase(updatedAccount)

        assertTrue(result.isSuccess)
        assertEquals(listOf(updatedAccount), accountRepository.updatedAccounts)
    }

    @Test
    fun `perbarui akun gagal - mengembalikan Result failure saat repository melempar exception`() = runTest {
        accountRepository.failUpdate = true
        val account = AccountModel(id = "acc1", name = "BCA", accountType = TypeAccount.BANK)

        val result = useCase(account)

        assertTrue(result.isFailure)
        assertEquals("error update account", result.exceptionOrNull()?.message)
    }

    @Test
    fun `perbarui akun dibatalkan - CancellationException diteruskan dan tidak ditelan`() = runTest {
        val repoWithCancellation = object : FakeAccountRepository() {
            override suspend fun updateAccount(account: AccountModel) {
                throw CancellationException("Coroutine dibatalkan")
            }
        }
        val useCaseWithCancellation = UpdateAccontUseCase(repoWithCancellation)
        val account = AccountModel(id = "acc1", name = "BCA", accountType = TypeAccount.BANK)

        var thrown = false
        try {
            useCaseWithCancellation(account)
        } catch (e: CancellationException) {
            thrown = true
        }
        assertTrue(thrown)
    }
}
