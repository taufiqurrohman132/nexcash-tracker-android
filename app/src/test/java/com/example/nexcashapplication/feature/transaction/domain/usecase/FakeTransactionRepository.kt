package com.example.nexcashapplication.feature.transaction.domain.usecase

import com.example.nexcashapplication.core.datasource.local.model.TransactionEntity
import com.example.nexcashapplication.core.datasource.local.result.CategoryExpenseResult
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.dashboard.domain.model.MonthlySummary
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory [TransactionRepository] khusus test [GetTransactionsByFilterUseCase].
 * Tiga metode yang dipakai use case (None / ByAccount / ByCategory) reaktif seperti
 * Room; metode lain sengaja melempar supaya gagal jelas bila tiba-tiba terpakai.
 */
class FakeTransactionRepository : TransactionRepository {

    private val withDetails = MutableStateFlow<List<TransactionWithDetails>>(emptyList())

    fun seed(vararg rows: TransactionWithDetails) {
        withDetails.value = withDetails.value + rows
    }

    override fun getAllTransactionsWithDetails(): Flow<List<TransactionWithDetails>> = withDetails

    override fun getTransactionsByAccount(accountId: String): Flow<List<TransactionWithDetails>> =
        withDetails.map { list -> list.filter { it.transaction.accountId == accountId } }

    override fun getTransactionsWithDetailsByCategoryIds(
        categoryIds: List<String>,
    ): Flow<List<TransactionWithDetails>> =
        withDetails.map { list -> list.filter { it.transaction.categoryId in categoryIds } }

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

    override fun getIncomeByCategoryByMonth(
        startOfMonth: Long,
        endOfMonth: Long,
    ): Flow<Map<String, Long>> = unused()

    override fun getUsedAmountByCategory(
        categoryId: String,
        periodStart: Long,
        periodEnd: Long,
    ): Flow<Long> = unused()

    override fun getUsedAmountsForCategories(
        categoryIds: List<String>,
        periodStart: Long,
        periodEnd: Long,
    ): Flow<List<CategoryExpenseResult>> = unused()

    override fun getTransactionsForCategories(categoryIds: List<String>): Flow<List<Transaction>> =
        unused()

    override fun getRecentTransactions(limit: Int): Flow<List<TransactionWithDetails>> = unused()

    override fun getRecentTransactionsByCategory(
        limit: Int,
        categoryId: String,
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
        throw UnsupportedOperationException("Tidak dipakai di unit test GetTransactionsByFilterUseCase")
}
