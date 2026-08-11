package com.example.nexcashapplication.feature.aset.data.fake

import com.example.nexcashapplication.core.datasource.local.model.AccountEntity
import com.example.nexcashapplication.feature.aset.data.dummy.DummyData
import com.example.nexcashapplication.feature.aset.data.mapper.toDomain
import com.example.nexcashapplication.feature.aset.domain.model.AccountModel
import com.example.nexcashapplication.feature.aset.domain.model.MonthlySummaryAccount
import com.example.nexcashapplication.feature.aset.domain.repository.AccountRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeAccountRepositoryImpl @Inject constructor() : AccountRepository {
    override fun getTotalBalance(): Flow<Long> {
        TODO("Not yet implemented")
    }

    override fun getDefaultAccount(): Flow<AccountEntity?> {
        TODO("Not yet implemented")
    }

    override fun getAllAccounts(): Flow<List<AccountModel>> {
        return flowOf(DummyData.accounts)
    }

    override fun getMonthlySummary(): Flow<MonthlySummaryAccount> {
        return flowOf(DummyData.monthlySummary.toDomain())
    }

    override fun getAccountById(accountId: String): Flow<AccountModel> {
        return flowOf(DummyData.accounts.find { it.id == accountId } ?: DummyData.accounts[0])
    }

    override suspend fun insertAccount(account: AccountModel) {
        TODO("Not yet implemented")
    }

    override suspend fun updateAccount(account: AccountModel) {
        TODO("Not yet implemented")
    }
}