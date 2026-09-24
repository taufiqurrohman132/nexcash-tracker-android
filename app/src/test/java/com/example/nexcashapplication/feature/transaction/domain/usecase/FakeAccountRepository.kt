package com.example.nexcashapplication.feature.transaction.domain.usecase

import com.example.nexcashapplication.feature.account.domain.model.AccountModel
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.account.domain.repository.AccountRepository
import com.example.nexcashapplication.feature.account.domain.model.MonthlySummaryAccount
import com.example.nexcashapplication.core.datasource.local.model.AccountEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory [AccountRepository] khusus test [GetTransactionsByFilterUseCase].
 * Hanya [getAccountById] (judul TopAppBar) yang reaktif; metode lain melempar.
 */
class FakeAccountRepository : AccountRepository {

    private val accounts = MutableStateFlow<List<AccountModel>>(emptyList())

    fun seed(vararg rows: AccountModel) {
        accounts.value = accounts.value + rows
    }

    override fun getAccountById(accountId: String): Flow<AccountModel> =
        accounts.map { list ->
            list.first { it.id == accountId }
        }

    override fun getTotalBalance(): Flow<Long> = unused()

    override fun getDefaultAccount(): Flow<AccountEntity?> = unused()

    override fun getAllAccounts(): Flow<List<AccountModel>> = unused()

    override fun getMonthlySummary(): Flow<MonthlySummaryAccount> = unused()

    override suspend fun insertAccount(account: AccountModel): Unit = unused()

    override suspend fun updateAccount(account: AccountModel): Unit = unused()

    override suspend fun deleteAccount(accountId: String): Unit = unused()

    private fun unused(): Nothing =
        throw UnsupportedOperationException("Tidak dipakai di unit test GetTransactionsByFilterUseCase")
}

/** Contoh pembuat akun agar test tidak repetitif. */
fun sampleBankAccount(id: String = "bca", name: String = "BCA") = AccountModel(
    id = id,
    accountType = TypeAccount.BANK,
    name = name,
)
