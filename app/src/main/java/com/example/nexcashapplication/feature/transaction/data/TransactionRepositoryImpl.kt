package com.example.nexcashapplication.feature.transaction.data

import com.example.nexcashapplication.core.datasource.local.dao.TransactionDao
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
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

    override suspend fun insertTransaction(transaction: Transaction) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTransaction(id: String) {
        TODO("Not yet implemented")
    }
}