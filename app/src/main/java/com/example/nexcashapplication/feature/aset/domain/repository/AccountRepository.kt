package com.example.nexcashapplication.feature.aset.domain.repository

import com.example.nexcashapplication.core.datasource.local.model.AccountEntity
import com.example.nexcashapplication.core.datasource.local.result.MonthlySummaryAccountResult
import com.example.nexcashapplication.feature.aset.domain.model.AccountModel
import kotlinx.coroutines.flow.Flow

interface AccountRepository {

    // Dashboard: total saldo semua rekening (dijumlah)
    fun getTotalBalance(): Flow<Long>

    // Dashboard: ambil akun/aset default
    fun getDefaultAccount(): Flow<AccountEntity?>

    // List semua akun/aset
    fun getAllAccounts(): Flow<List<AccountModel>>
    fun getMonthlySummary(): Flow<MonthlySummaryAccountResult>
}