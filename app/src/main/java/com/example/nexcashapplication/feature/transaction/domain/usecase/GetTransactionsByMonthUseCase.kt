package com.example.nexcashapplication.feature.transaction.domain.usecase

import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.repository.TransactionRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetTransactionsByMonthUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {
    operator fun invoke(yearMonth: String): Flow<List<Transaction>> =
        repository.getTransactionsByMonth(yearMonth)
}