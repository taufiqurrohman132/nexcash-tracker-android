package com.example.nexcashapplication.feature.account.domain.usecase

import com.example.nexcashapplication.feature.account.domain.model.AccountModel
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.transaction.domain.usecase.FakeAccountRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeleteAccountUseCaseTest {

    private lateinit var accountRepository: FakeAccountRepository
    private lateinit var useCase: DeleteAccountUseCase

    @BeforeEach
    fun setUp() {
        accountRepository = FakeAccountRepository()
        useCase = DeleteAccountUseCase(accountRepository)
    }

    @Test
    fun `hapus akun - memanggil repository dan menghapus akun dari daftar`() = runTest {
        val account = AccountModel(id = "acc1", name = "BCA", accountType = TypeAccount.BANK)
        accountRepository.seed(account)

        useCase("acc1")

        assertEquals(listOf("acc1"), accountRepository.deletedAccountIds)
        assertTrue(accountRepository.getAllAccounts().first().isEmpty())
    }
}
