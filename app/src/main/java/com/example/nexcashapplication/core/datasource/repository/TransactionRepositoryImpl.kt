package com.example.nexcashapplication.core.datasource.repository

import android.util.Log
import androidx.room.withTransaction
import com.example.nexcashapplication.core.common.toEndOfMonthMillis
import com.example.nexcashapplication.core.common.toStartOfMonthMillis
import com.example.nexcashapplication.core.datasource.local.dao.AccountDao
import com.example.nexcashapplication.core.datasource.local.dao.CategoryDao
import com.example.nexcashapplication.core.datasource.local.dao.TransactionDao
import com.example.nexcashapplication.core.datasource.local.database.NexCashDatabase
import com.example.nexcashapplication.core.datasource.local.mapper.toDomain
import com.example.nexcashapplication.core.datasource.local.model.TransactionEntity
import com.example.nexcashapplication.core.datasource.local.result.CategoryExpenseResult
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.dashboard.domain.model.MonthlySummary
import com.example.nexcashapplication.feature.transaction.data.mapper.toDomain
import com.example.nexcashapplication.feature.transaction.data.mapper.toEntity
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.YearMonth
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    val db: NexCashDatabase,
    val transactionDao: TransactionDao,
    val accountDao: AccountDao,
    val categoryDao: CategoryDao,
) : TransactionRepository {

    /**
     * "yyyy-MM" → (awal, akhir) bulan itu dalam epoch millis UTC.
     *
     * Sama persis artinya dengan `strftime('%Y-%m', transaction_date/1000, 'unixepoch')`
     * yang dipakai sebelumnya (UTC, tanpa modifier 'localtime'), tapi rentang angka
     * bisa memakai index `transaction_date` — strftime tidak bisa.
     * Gagal parse jatuh ke bulan berjalan supaya query tetap valid.
     */
    private fun String.toUtcMonthRange(): Pair<Long, Long> {
        val month = runCatching { YearMonth.parse(this) }.getOrDefault(YearMonth.now())
        return month.toStartOfMonthMillis() to month.toEndOfMonthMillis()
    }

    override fun getAllTransactions(): Flow<List<Transaction>> {
        TODO("Not yet implemented")
    }

    override fun getTransactionById(id: String): Flow<Transaction> {
        TODO("Not yet implemented")
    }

    override fun getTransactionWithDetailsById(id: String): Flow<TransactionWithDetails?> {
        return transactionDao.getTransactionWithDetailsById(id).map { it?.toDomain() }
                .flowOn(Dispatchers.Default)
    }

    override fun getCountTransactionsByAccount(accountId: String): Flow<Int> {
        return transactionDao.getCountTransactionsByAccount(accountId)
    }

    override suspend fun countTransactionsInRange(start: Long, end: Long): Int {
        return transactionDao.countInRange(start, end)
    }

    override suspend fun getTransactionByIdOnce(id: String): TransactionEntity? {
        TODO("Not yet implemented")
    }

    override fun searchTransactions(query: String): Flow<List<TransactionWithDetails>> {
        return transactionDao.searchTransactions(query).map { list -> list.map { it.toDomain() } }
                .flowOn(Dispatchers.Default)
    }

    override fun getObserverTransactionsByMonth(yearMonth: String): Flow<List<Transaction>> {
        val (startOfMonth, endOfMonth) = yearMonth.toUtcMonthRange()
        return transactionDao.getTransactionsByMonth(
            startOfMonth, endOfMonth,
        ).map { list -> list.map { it.toDomain() } }
                .flowOn(Dispatchers.Default)
    }

    override fun getObserverTransactionsWithDetailsByMonth(
        yearMonth: String,
        categoryFilter: String?,
    ): Flow<List<TransactionWithDetails>> {
        val (startOfMonth, endOfMonth) = yearMonth.toUtcMonthRange()
        return transactionDao.getAllTransactionWithDetailsByMonth(
            startOfMonth, endOfMonth, categoryFilter,
        )
                // mapping entity → domain (alokasi per baris) jangan di main thread:
                // ini yang bikin transisi pindah tab tersendat
                .map { list -> list.map { it.toDomain() } }
                .flowOn(Dispatchers.Default)
    }

    override fun getTotalExpenseByMonth(startOfMonth: Long, endOfMonth: Long): Flow<Long> {
        return transactionDao.getTotalExpenseInRange(startOfMonth = startOfMonth, endOfMonth = endOfMonth)
    }

    override fun getExpenseByCategoryByMonth(startOfMonth: Long, endOfMonth: Long): Flow<Map<String, Long>> =
        transactionDao.getExpenseByCategoryInRange(startOfMonth, endOfMonth)
                .map { rows -> rows.associate { it.categoryId to it.total } }
                .flowOn(Dispatchers.Default)

    override fun getIncomeByCategoryByMonth(startOfMonth: Long, endOfMonth: Long): Flow<Map<String, Long>> =
        transactionDao.getIncomeByCategoryInRange(startOfMonth, endOfMonth)
                .map { rows -> rows.associate { it.categoryId to it.total } }
                .flowOn(Dispatchers.Default)

    override fun getUsedAmountByCategory(
        categoryId: String,
        periodStart: Long,
        periodEnd: Long,
    ): Flow<Long> {
        return transactionDao.getUsedAmountByCategory(categoryId, periodStart, periodEnd)
    }

    override fun getUsedAmountsForCategories(
        categoryIds: List<String>,
        periodStart: Long,
        periodEnd: Long,
    ): Flow<List<CategoryExpenseResult>> {
        return transactionDao.getUsedAmountsForCategories(categoryIds, periodStart, periodEnd)
    }

    override fun getTransactionsForCategories(categoryIds: List<String>): Flow<List<Transaction>> {
        return transactionDao.getTransactionsForCategories(categoryIds)
                .map { list -> list.map { it.toDomain() } }
                .flowOn(Dispatchers.Default)
    }


    override suspend fun insertTransaction(transaction: Transaction) {
        db.withTransaction {
//            categoryDao.adjustUsedBalance(
//                categoryId = transaction.categoryId ?: "",
//                delta = transaction.amount,
//            )
            Log.d("INSERTTRANS", "insertTransaction: ${transaction.categoryId}")

            accountDao.adjustBalance(
                accountId = transaction.accountId ?: "",
                delta = transaction.applyAmount(),
            )

            transactionDao.insertTransaction(transaction.toEntity())
        }
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transactionId: String) {
        db.withTransaction {
            val transaction = transactionDao.getTransactionByIdOnce(transactionId)?.toDomain()
                ?: return@withTransaction // udah kehapus / nggak ada, no-op

            // reverse account
            accountDao.adjustBalance(
                accountId = transaction.accountId ?: "",
                delta = transaction.reverseAmount(),
            )

            transactionDao.deleteTransaction(transactionId, System.currentTimeMillis())
        }
    }

    override fun getMonthlySummary(yearMonth: String): Flow<MonthlySummary> {
        val (startOfMonth, endOfMonth) = yearMonth.toUtcMonthRange()
        return transactionDao.getMonthlySummary(startOfMonth, endOfMonth).map {
            it.toDomain(yearMonth)
        }.flowOn(Dispatchers.Default)
    }

    override fun getRecentTransactions(limit: Int): Flow<List<TransactionWithDetails>> {
        return transactionDao.getRecentTransactions(limit).map { list -> list.map { it.toDomain() } }
                .flowOn(Dispatchers.Default)
    }

    override fun getRecentTransactionsByCategory(limit: Int, categoryId: String): Flow<List<TransactionWithDetails>> {
        return transactionDao.getRecentTransactionsByCategory(limit, categoryId)
                .map { list -> list.map { it.toDomain() } }
                .flowOn(Dispatchers.Default)
    }

    override fun getAllTransactionsWithDetails(): Flow<List<TransactionWithDetails>> {
        return transactionDao.getAllTransactionWithDetails().map { list -> list.map { it.toDomain() } }
                .flowOn(Dispatchers.Default)
    }

    override fun getTransactionsByAccount(accountId: String): Flow<List<TransactionWithDetails>> {
        return transactionDao.getTransactionsByAccount(accountId)
                .map { list -> list.map { it.toDomain() } }
                .flowOn(Dispatchers.Default)
    }

    override fun getTransactionsWithDetailsByCategoryIds(
        categoryIds: List<String>,
    ): Flow<List<TransactionWithDetails>> {
        return transactionDao.getTransactionsWithDetailsByCategoryIds(categoryIds)
                .map { list -> list.map { it.toDomain() } }
                .flowOn(Dispatchers.Default)
    }

}
