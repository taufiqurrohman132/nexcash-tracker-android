package com.example.nexcashapplication.feature.transaction.domain.usecase

import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import com.example.nexcashapplication.feature.transaction.domain.repository.TransactionRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetTransactionsWithDetailsByMonthUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {
    operator fun invoke(
        yearMonth: String,
        categoryFilterId: String? = null,
    ): Flow<List<TransactionWithDetails>> =
        repository.getObserverTransactionsWithDetailsByMonth(yearMonth, categoryFilterId)
}