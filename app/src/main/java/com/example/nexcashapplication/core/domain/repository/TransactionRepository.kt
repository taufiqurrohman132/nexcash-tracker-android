package com.example.nexcashapplication.core.domain.repository

import com.example.nexcashapplication.core.datasource.local.model.TransactionEntity
import com.example.nexcashapplication.feature.dashboard.domain.model.MonthlySummary
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getMonthlySummary(
        yearMonth: String,
    ): Flow<MonthlySummary>  // saldo, pemasukan, pengeluaran

    fun getObserverTransactionsByMonth(yearMonth: String): Flow<List<Transaction>>
    fun getObserverTransactionsWithDetailsByMonth(
        yearMonth: String,
        categoryFilter: String?,
    ): Flow<List<TransactionWithDetails>>

    fun getTotalExpenseByMonth(startOfMonth: Long, endOfMonth: Long): Flow<Long>
    fun getExpenseByCategoryByMonth(
        startOfMonth: Long,
        endOfMonth: Long,
    ): Flow<Map<String, Long>> // categoryId -> total

    fun getUsedAmountByCategory(categoryId: String, periodStart: Long, periodEnd: Long): Flow<Long>
    fun getTransactionsForCategories(categoryIds: List<String>): Flow<List<Transaction>>

    fun getRecentTransactionsByCategory(limit: Int, categoryId: String?): Flow<List<TransactionWithDetails>>

    /** Semua transaksi (belum dihapus) + detail akun/kategori — filter `None` di halaman Semua Transaksi. */
    fun getAllTransactionsWithDetails(): Flow<List<TransactionWithDetails>>

    /** Transaksi milik satu akun (belum dihapus) — filter `ByAccount` di halaman Semua Transaksi. */
    fun getTransactionsByAccount(accountId: String): Flow<List<TransactionWithDetails>>

    /** Transaksi kategori mana pun dalam [categoryIds] (induk + sub) — filter `ByCategory`. */
    fun getTransactionsWithDetailsByCategoryIds(categoryIds: List<String>): Flow<List<TransactionWithDetails>>

    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionById(id: String): Flow<Transaction>
    fun getTransactionWithDetailsById(id: String): Flow<TransactionWithDetails?>
    fun getCountTransactionsByAccount(accountId: String): Flow<Int>
    suspend fun getTransactionByIdOnce(id: String): TransactionEntity?

    fun searchTransactions(query: String): Flow<List<TransactionWithDetails>>

    suspend fun insertTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transactionId: String)
}