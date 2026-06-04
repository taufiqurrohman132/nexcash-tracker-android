package com.example.nexcashapplication.feature.category.domain.repository

import com.example.nexcashapplication.core.datasource.local.model.TransactionEntity
import com.example.nexcashapplication.feature.dashboard.domain.model.MonthlySummary
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getMonthlySummary(bulan: Int, tahun: Int): Flow<MonthlySummary>  // saldo, pemasukan, pengeluaran
    fun getRecentTransactions(limit: Int): Flow<List<TransactionEntity>>
//    fun getBudgetStatus(): Flow<BudgetStatus>
}