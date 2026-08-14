package com.example.nexcashapplication.feature.aset.data

import com.example.nexcashapplication.core.datasource.local.dao.AccountDao
import com.example.nexcashapplication.core.datasource.local.model.AccountEntity
import com.example.nexcashapplication.feature.aset.data.mapper.toDomain
import com.example.nexcashapplication.feature.aset.data.mapper.toEntity
import com.example.nexcashapplication.feature.aset.domain.model.AccountModel
import com.example.nexcashapplication.feature.aset.domain.model.MonthlySummaryAccount
import com.example.nexcashapplication.feature.aset.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    override fun getAllAccounts(): Flow<List<AccountModel>> {
        return dao.getAllAccounts().map { list -> list.map { it.toDomain() } }
    }

    override fun getMonthlySummary(): Flow<MonthlySummaryAccount> {
        return dao.getMonthlySummary().map { it.toDomain() }
    }

    override fun getAccountById(accountId: String): Flow<AccountModel> {
        return dao.getAccountById(accountId).map { it.toDomain() }
    }


    override suspend fun insertAccount(account: AccountModel) {
        dao.insertAccount(account.toEntity())
    }

    override suspend fun updateAccount(account: AccountModel) {
        dao.updateAccount(account.toEntity())
    }

}
