package com.example.nexcashapplication.feature.account.data

import com.example.nexcashapplication.core.common.toEndOfMonthMillis
import com.example.nexcashapplication.core.common.toStartOfMonthMillis
import com.example.nexcashapplication.core.datasource.local.dao.AccountDao
import com.example.nexcashapplication.core.datasource.local.model.AccountEntity
import com.example.nexcashapplication.feature.account.data.mapper.toDomain
import com.example.nexcashapplication.feature.account.data.mapper.toEntity
import com.example.nexcashapplication.feature.account.domain.model.AccountModel
import com.example.nexcashapplication.feature.account.domain.model.MonthlySummaryAccount
import com.example.nexcashapplication.feature.account.domain.repository.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.YearMonth
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    val dao: AccountDao,
) : AccountRepository {
    override fun getTotalBalance(): Flow<Long> {
        return dao.getTotalBalance()
    }

    override fun getDefaultAccount(): Flow<AccountEntity?> {
        return dao.getDefaultAccount()
    }

    override fun getAllAccounts(): Flow<List<AccountModel>> =
        // mapping entity → domain di luar main thread (layar Asset kumpulkan
        // ini bersamaan dengan transisi pindah tab)
        dao.getAllAccounts()
                .map { list -> list.map { it.toDomain() } }
                .flowOn(Dispatchers.Default)

    override fun getMonthlySummary(): Flow<MonthlySummaryAccount> {
        // "bulan berjalan" ditentukan saat flow dibentuk — sama seperti perilaku
        // lama strftime('now'), tapi rentang angka bisa memakai index transaction_date
        val month = YearMonth.now()
        return dao.getMonthlySummary(
            startOfMonth = month.toStartOfMonthMillis(),
            endOfMonth = month.toEndOfMonthMillis(),
        ).map { it.toDomain() }
                .flowOn(Dispatchers.Default)
    }

    override fun getAccountById(accountId: String): Flow<AccountModel> =
        dao.getAccountById(accountId).map { it.toDomain() }.flowOn(Dispatchers.Default)


    override suspend fun insertAccount(account: AccountModel) {
        dao.insertAccount(account.toEntity())
    }

    override suspend fun updateAccount(account: AccountModel) {
        dao.updateAccount(account.toEntity())
    }

    override suspend fun deleteAccount(accountId: String) {
        dao.deleteAccountRow(accountId)
    }

}
