package com.example.nexcashapplication.core.domain.repository

import com.example.nexcashapplication.core.datasource.local.model.TransactionEntity
import com.example.nexcashapplication.core.datasource.local.result.CategoryExpenseResult
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

    /** Total pengeluaran per kategori pada rentang bulan — donut Realisasi (Pengeluaran). */
    fun getExpenseByCategoryByMonth(
        startOfMonth: Long,
        endOfMonth: Long,
    ): Flow<Map<String, Long>> // categoryId -> total

    /** Total pemasukan per kategori pada rentang bulan — donut Realisasi (Pendapatan). */
    fun getIncomeByCategoryByMonth(
        startOfMonth: Long,
        endOfMonth: Long,
    ): Flow<Map<String, Long>> // categoryId -> total

    fun getUsedAmountByCategory(categoryId: String, periodStart: Long, periodEnd: Long): Flow<Long>

    /**
     * Jumlah terpakai per kategori dalam window — batch untuk rollup induk
     * (induk + sub) dan rincian sub kategori. Kategori tanpa transaksi tidak muncul.
     */
    fun getUsedAmountsForCategories(
        categoryIds: List<String>,
        periodStart: Long,
        periodEnd: Long,
    ): Flow<List<CategoryExpenseResult>>

    fun getTransactionsForCategories(categoryIds: List<String>): Flow<List<Transaction>>

    /** Transaksi terakhir **tanpa** filter kategori — daftar "Transaksi Terakhir" di dashboard. */
    fun getRecentTransactions(limit: Int): Flow<List<TransactionWithDetails>>

    /** Transaksi terakhir satu kategori — daftar "Transaksi Terakhir" di detail kategori. */
    fun getRecentTransactionsByCategory(limit: Int, categoryId: String): Flow<List<TransactionWithDetails>>

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