package com.example.nexcashapplication.feature.category.presentation.main

import com.example.nexcashapplication.core.datasource.local.model.TransactionEntity
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.dashboard.domain.model.MonthlySummary
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory [TransactionRepository] untuk unit test (fitur kategori: main & detail).
 *
 * Flow reaktif:
 * - [getTransactionsForCategories] dari [seed] (dipakai CategoryViewModel),
 * - [getRecentTransactionsByCategory] dari [seedWithDetails] (urut terbaru,
 *   hormati `limit`; `limit < 0` = tanpa batas seperti SQL-nya),
 * - [getUsedAmountByCategory] dari [seed] (EXPENSE dalam window periode).
 *
 * Metode lain sengaja melempar — bila suatu saat dipanggil test akan gagal jelas,
 * bukan diam-diam return data palsu.
 */
class FakeTransactionRepository : TransactionRepository {

    private val transactions = MutableStateFlow<List<Transaction>>(emptyList())
    private val withDetails = MutableStateFlow<List<TransactionWithDetails>>(emptyList())

    fun seed(vararg rows: Transaction) {
        transactions.value = transactions.value + rows
    }

    /** Seed transaksi + relasi akun/kategori (untuk daftar "transaksi terakhir"). */
    fun seedWithDetails(vararg rows: TransactionWithDetails) {
        withDetails.value = withDetails.value + rows
    }

    override fun getTransactionsForCategories(categoryIds: List<String>): Flow<List<Transaction>> =
        transactions.map { list -> list.filter { it.categoryId in categoryIds } }

    override fun getRecentTransactionsByCategory(
        limit: Int,
        categoryId: String?,
    ): Flow<List<TransactionWithDetails>> =
        withDetails.map { list ->
            list.filter { categoryId == null || it.transaction.categoryId == categoryId }
                .sortedByDescending { it.transaction.dateTime }
                .let { rows -> if (limit < 0) rows else rows.take(limit) }
        }

    override fun getUsedAmountByCategory(
        categoryId: String,
        periodStart: Long,
        periodEnd: Long,
    ): Flow<Long> =
        transactions.map { list ->
            list.filter {
                it.categoryId == categoryId &&
                        it.typeTransaction == TransactionType.EXPENSE &&
                        it.dateTime.toEpochMilliseconds() in periodStart..periodEnd
            }.sumOf { it.amount }
        }

    override fun getMonthlySummary(yearMonth: String): Flow<MonthlySummary> = unused()

    override fun getObserverTransactionsByMonth(yearMonth: String): Flow<List<Transaction>> =
        unused()

    override fun getObserverTransactionsWithDetailsByMonth(
        yearMonth: String,
        categoryFilter: String?,
    ): Flow<List<TransactionWithDetails>> = unused()

    override fun getTotalExpenseByMonth(startOfMonth: Long, endOfMonth: Long): Flow<Long> =
        unused()

    override fun getExpenseByCategoryByMonth(
        startOfMonth: Long,
        endOfMonth: Long,
    ): Flow<Map<String, Long>> = unused()

    override fun getAllTransactionsWithDetails(): Flow<List<TransactionWithDetails>> = unused()

    override fun getTransactionsByAccount(accountId: String): Flow<List<TransactionWithDetails>> =
        unused()

    override fun getTransactionsWithDetailsByCategoryIds(
        categoryIds: List<String>,
    ): Flow<List<TransactionWithDetails>> = unused()

    override fun getAllTransactions(): Flow<List<Transaction>> = unused()

    override fun getTransactionById(id: String): Flow<Transaction> = unused()

    override fun getTransactionWithDetailsById(id: String): Flow<TransactionWithDetails?> =
        unused()

    override fun getCountTransactionsByAccount(accountId: String): Flow<Int> = unused()

    override suspend fun getTransactionByIdOnce(id: String): TransactionEntity = unused()

    override fun searchTransactions(query: String): Flow<List<TransactionWithDetails>> =
        unused()

    override suspend fun insertTransaction(transaction: Transaction): Unit = unused()

    override suspend fun updateTransaction(transaction: Transaction): Unit = unused()

    override suspend fun deleteTransaction(transactionId: String): Unit = unused()

    private fun unused(): Nothing =
        throw UnsupportedOperationException("Tidak dipakai di unit test fitur kategori")
}
