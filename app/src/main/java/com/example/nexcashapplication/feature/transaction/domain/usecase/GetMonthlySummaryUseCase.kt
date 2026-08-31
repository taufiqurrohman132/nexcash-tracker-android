package com.example.nexcashapplication.feature.transaction.domain.usecase

import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.dashboard.domain.model.MonthlySummary
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMonthlySummaryUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {
    operator fun invoke(yearMonth: String): Flow<MonthlySummary> =
        repository.getMonthlySummary(yearMonth)
}