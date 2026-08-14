package com.example.nexcashapplication.feature.aset.domain.repository

import com.example.nexcashapplication.core.datasource.local.model.AccountEntity
import com.example.nexcashapplication.feature.aset.domain.model.AccountModel
import com.example.nexcashapplication.feature.aset.domain.model.MonthlySummaryAccount
import kotlinx.coroutines.flow.Flow

interface AccountRepository {

    // Dashboard: total saldo semua rekening (dijumlah)
    fun getTotalBalance(): Flow<Long>

    // Dashboard: ambil akun/aset default
    fun getDefaultAccount(): Flow<AccountEntity?>

    // List semua akun/aset
    fun getAllAccounts(): Flow<List<AccountModel>>
    fun getMonthlySummary(): Flow<MonthlySummaryAccount>

    fun getAccountById(accountId: String): Flow<AccountModel>


    suspend fun insertAccount(account: AccountModel)

    suspend fun updateAccount(account: AccountModel)
}