package com.example.nexcashapplication.core.datasource.repository

import android.util.Log
import androidx.room.withTransaction
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    val db: NexCashDatabase,
    val transactionDao: TransactionDao,
    val accountDao: AccountDao,
    val categoryDao: CategoryDao,
) : TransactionRepository {
    override fun getAllTransactions(): Flow<List<Transaction>> {
        TODO("Not yet implemented")
    }

    override fun getTransactionById(id: String): Flow<Transaction> {
        TODO("Not yet implemented")
    }

    override fun getTransactionWithDetailsById(id: String): Flow<TransactionWithDetails?> {
        return transactionDao.getTransactionWithDetailsById(id).map { it?.toDomain() }
    }

    override fun getCountTransactionsByAccount(accountId: String): Flow<Int> {
        return transactionDao.getCountTransactionsByAccount(accountId)
    }

    override suspend fun getTransactionByIdOnce(id: String): TransactionEntity? {
        TODO("Not yet implemented")
    }

    override fun searchTransactions(query: String): Flow<List<TransactionWithDetails>> {
        return transactionDao.searchTransactions(query).map { list -> list.map { it.toDomain() } }
    }

    override fun getObserverTransactionsByMonth(yearMonth: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByMonth(
            yearMonth,
        ).map { list -> list.map { it.toDomain() } }
    }

    override fun getObserverTransactionsWithDetailsByMonth(
        yearMonth: String,
        categoryFilter: String?,
    ): Flow<List<TransactionWithDetails>> {
        return transactionDao.getAllTransactionWithDetailsByMonth(
            yearMonth, categoryFilter,
        ).map { list -> list.map { it.toDomain() } }
    }

    override fun getTotalExpenseByMonth(startOfMonth: Long, endOfMonth: Long): Flow<Long> {
        return transactionDao.getTotalExpenseInRange(startOfMonth = startOfMonth, endOfMonth = endOfMonth)
    }

    override fun getExpenseByCategoryByMonth(startOfMonth: Long, endOfMonth: Long): Flow<Map<String, Long>> {
        TODO("Not yet implemented")
    }

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
        return transactionDao.getMonthlySummary(yearMonth).map {
            it.toDomain(yearMonth)
        }
    }

    override fun getRecentTransactionsByCategory(limit: Int, categoryId: String?): Flow<List<TransactionWithDetails>> {
        return transactionDao.getRecentTransactionsByCategory(limit, categoryId).map { list -> list.map { it.toDomain() } }
    }

    override fun getAllTransactionsWithDetails(): Flow<List<TransactionWithDetails>> {
        return transactionDao.getAllTransactionWithDetails().map { list -> list.map { it.toDomain() } }
    }

    override fun getTransactionsByAccount(accountId: String): Flow<List<TransactionWithDetails>> {
        return transactionDao.getTransactionsByAccount(accountId)
                .map { list -> list.map { it.toDomain() } }
    }

    override fun getTransactionsWithDetailsByCategoryIds(
        categoryIds: List<String>,
    ): Flow<List<TransactionWithDetails>> {
        return transactionDao.getTransactionsWithDetailsByCategoryIds(categoryIds)
                .map { list -> list.map { it.toDomain() } }
    }

}