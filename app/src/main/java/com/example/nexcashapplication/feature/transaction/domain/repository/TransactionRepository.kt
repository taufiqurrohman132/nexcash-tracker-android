package com.example.nexcashapplication.feature.transaction.domain.repository

import com.example.nexcashapplication.core.datasource.local.model.TransactionEntity
import com.example.nexcashapplication.feature.dashboard.domain.model.MonthlySummary
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getMonthlySummary(
        yearMonth: String,
    ): Flow<MonthlySummary>  // saldo, pemasukan, pengeluaran

    fun getTransactionsByMonth(yearMonth: String): Flow<List<Transaction>>
    fun getTransactionsWithDetailsByMonth(yearMonth: String): Flow<List<TransactionWithDetails>>

    fun getRecentTransactions(limit: Int): Flow<List<TransactionEntity>>

    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionById(id: String): Flow<Transaction>
    fun getTransactionWithDetailsById(id: String): Flow<TransactionWithDetails>
    fun getCountTransactionsByAccount(accountId: String): Flow<Int>
    fun searchTransactions(query: String): Flow<List<TransactionWithDetails>>

    suspend fun insertTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(id: String)
}
