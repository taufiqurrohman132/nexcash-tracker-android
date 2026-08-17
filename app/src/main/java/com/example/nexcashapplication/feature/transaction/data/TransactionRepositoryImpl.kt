package com.example.nexcashapplication.feature.transaction.data

import com.example.nexcashapplication.core.datasource.local.dao.TransactionDao
import com.example.nexcashapplication.core.datasource.local.mapper.toDomain
import com.example.nexcashapplication.core.datasource.local.model.TransactionEntity
import com.example.nexcashapplication.feature.dashboard.domain.model.MonthlySummary
import com.example.nexcashapplication.feature.transaction.data.mapper.toDomain
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import com.example.nexcashapplication.feature.transaction.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    val dao: TransactionDao,
) : TransactionRepository {
    override fun getAllTransactions(): Flow<List<Transaction>> {
        TODO("Not yet implemented")
    }

    override fun getTransactionById(id: String): Flow<Transaction?> {
        TODO("Not yet implemented")
    }

    override fun getCountTransactionsByAccount(accountId: String): Flow<Int> {
        return dao.getCountTransactionsByAccount(accountId)
    }

    override fun getTransactionsByMonth(yearMonth: String): Flow<List<Transaction>> {
        return dao.getTransactionsByMonth(
            yearMonth,
        ).map { list -> list.map { it.toDomain() } }
    }

    override fun getTransactionsWithDetailsByMonth(yearMonth: String): Flow<List<TransactionWithDetails>> {
        return dao.getAllTransactionWithDetailsByMonth(
            yearMonth,
        ).map { list -> list.map { it.toDomain() } }

    }

    override suspend fun insertTransaction(transaction: Transaction) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTransaction(id: String) {
        TODO("Not yet implemented")
    }

    override fun getMonthlySummary(yearMonth: String): Flow<MonthlySummary> {
        return dao.getMonthlySummary(yearMonth).map {
            it.toDomain(yearMonth)
        }
    }

    override fun getRecentTransactions(limit: Int): Flow<List<TransactionEntity>> {
        return dao.getRecentTransactions(10)
    }

}