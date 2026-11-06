package com.example.nexcashapplication.feature.transaction.domain.usecase

import com.example.nexcashapplication.core.datasource.local.model.AccountEntity
import com.example.nexcashapplication.feature.account.domain.model.AccountModel
import com.example.nexcashapplication.feature.account.domain.model.MonthlySummaryAccount
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.account.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * In-memory [AccountRepository] untuk unit test fitur transaksi & akun.
 */
open class FakeAccountRepository : AccountRepository {

    val accounts = MutableStateFlow<List<AccountModel>>(emptyList())
    val monthlySummary = MutableStateFlow(
        MonthlySummaryAccount(
            totalBalance = 0L,
            activeAccount = 0,
            monthlyIncome = 0L,
            monthlyExpense = 0L,
        ),
    )

    var failGetAll = false
    var failGetById = false
    var failInsert = false
    var failUpdate = false
    var failDelete = false
    var failMonthlySummary = false

    val insertedAccounts = mutableListOf<AccountModel>()
    val updatedAccounts = mutableListOf<AccountModel>()
    val deletedAccountIds = mutableListOf<String>()

    fun seed(vararg rows: AccountModel) {
        accounts.value = accounts.value + rows
    }

    override fun getAccountById(accountId: String): Flow<AccountModel> =
        if (failGetById) flow { error("error load account") }
        else accounts.map { list -> list.first { it.id == accountId } }

    override fun getTotalBalance(): Flow<Long> =
        accounts.map { list -> list.sumOf { it.currentBalance } }

    override fun getDefaultAccount(): Flow<AccountEntity?> = unused()

    override fun getAllAccounts(): Flow<List<AccountModel>> =
        if (failGetAll) flow { error("error get all accounts") }
        else accounts

    override fun getMonthlySummary(): Flow<MonthlySummaryAccount> =
        if (failMonthlySummary) flow { error("error monthly summary") }
        else monthlySummary

    override suspend fun insertAccount(account: AccountModel) {
        if (failInsert) error("error insert account")
        insertedAccounts += account
        accounts.value = accounts.value + account
    }

    override suspend fun updateAccount(account: AccountModel) {
        if (failUpdate) error("error update account")
        updatedAccounts += account
        accounts.value = accounts.value.map { if (it.id == account.id) account else it }
    }

    override suspend fun deleteAccount(accountId: String) {
        if (failDelete) error("error delete account")
        deletedAccountIds += accountId
        accounts.value = accounts.value.filterNot { it.id == accountId }
    }

    private fun unused(): Nothing =
        throw UnsupportedOperationException("Tidak dipakai di unit test")
}

/** Contoh pembuat akun agar test tidak repetitif. */
fun sampleBankAccount(
    id: String = "bca",
    name: String = "BCA",
    balance: Long = 0L,
    accountType: TypeAccount = TypeAccount.BANK,
    lastUpdateAt: Long = 0L,
) = AccountModel(
    id = id,
    accountType = accountType,
    name = name,
    currentBalance = balance,
    lastUpdateAt = lastUpdateAt,
)
