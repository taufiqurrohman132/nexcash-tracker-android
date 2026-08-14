package com.example.nexcashapplication.feature.transaction.domain.repository

import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionById(id: String): Flow<Transaction?>
    fun getCountTransactionsByAccount(accountId: String): Flow<Int>

    suspend fun insertTransaction(transaction: Transaction)
    suspend fun deleteTransaction(id: String)
}
