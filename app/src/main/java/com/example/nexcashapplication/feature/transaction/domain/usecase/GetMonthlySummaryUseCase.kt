package com.example.nexcashapplication.feature.transaction.domain.usecase

import com.example.nexcashapplication.feature.dashboard.domain.model.MonthlySummary
import com.example.nexcashapplication.feature.transaction.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMonthlySummaryUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {
    operator fun invoke(yearMonth: String): Flow<MonthlySummary> =
        repository.getMonthlySummary(yearMonth)
}