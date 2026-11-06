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

class SaveAccontUseCaseTest {

    private lateinit var accountRepository: FakeAccountRepository
    private lateinit var useCase: SaveAccontUseCase

    @BeforeEach
    fun setUp() {
        accountRepository = FakeAccountRepository()
        useCase = SaveAccontUseCase(accountRepository)
    }

    @Test
    fun `simpan akun berhasil - mengembalikan Result success dan tersimpan di repository`() = runTest {
        val account = AccountModel(id = "acc1", name = "Mandiri", accountType = TypeAccount.BANK)

        val result = useCase(account)

        assertTrue(result.isSuccess)
        assertEquals(listOf(account), accountRepository.insertedAccounts)
    }

    @Test
    fun `simpan akun gagal - mengembalikan Result failure saat repository melempar exception`() = runTest {
        accountRepository.failInsert = true
        val account = AccountModel(id = "acc1", name = "Mandiri", accountType = TypeAccount.BANK)

        val result = useCase(account)

        assertTrue(result.isFailure)
        assertEquals("error insert account", result.exceptionOrNull()?.message)
    }

    @Test
    fun `simpan akun dibatalkan - CancellationException diteruskan dan tidak ditelan`() = runTest {
        val repoWithCancellation = object : FakeAccountRepository() {
            override suspend fun insertAccount(account: AccountModel) {
                throw CancellationException("Coroutine dibatalkan")
            }
        }
        val useCaseWithCancellation = SaveAccontUseCase(repoWithCancellation)
        val account = AccountModel(id = "acc1", name = "Mandiri", accountType = TypeAccount.BANK)

        var thrown = false
        try {
            useCaseWithCancellation(account)
        } catch (e: CancellationException) {
            thrown = true
        }
        assertTrue(thrown)
    }
}
