package com.example.nexcashapplication.feature.category.presentation.main

import com.example.nexcashapplication.core.datasource.local.model.TransactionEntity
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.dashboard.domain.model.MonthlySummary
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory [TransactionRepository] untuk unit test.
 * Hanya [getTransactionsForCategories] (dipakai CategoryViewModel) yang reaktif;
 * metode lain sengaja melempar — bila suatu saat dipanggil test akan gagal jelas,
 * bukan diam-diam return data palsu.
 */
class FakeTransactionRepository : TransactionRepository {

    private val transactions = MutableStateFlow<List<Transaction>>(emptyList())

    fun seed(vararg rows: Transaction) {
        transactions.value = transactions.value + rows
    }

    override fun getTransactionsForCategories(categoryIds: List<String>): Flow<List<Transaction>> =
        transactions.map { list -> list.filter { it.categoryId in categoryIds } }

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

    override fun getUsedAmountByCategory(
        categoryId: String,
        periodStart: Long,
        periodEnd: Long,
    ): Flow<Long> = unused()

    override fun getRecentTransactionsByCategory(
        limit: Int,
        categoryId: String?,
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
        throw UnsupportedOperationException("Tidak dipakai di unit test CategoryViewModel")
}
