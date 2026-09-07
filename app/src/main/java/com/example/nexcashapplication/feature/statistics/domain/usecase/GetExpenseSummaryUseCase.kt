package com.example.nexcashapplication.feature.statistics.domain.usecase

import com.example.nexcashapplication.core.common.previousMonthOf
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.statistics.domain.model.ExpenseSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetExpenseSummaryUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(monthStart: Long, monthEnd: Long): Flow<ExpenseSummary> {
        val (prevMonth, prevYear) = previousMonthOf(monthStart, monthEnd)

        val currentTotal = transactionRepository.getTotalExpenseByMonth(monthStart, monthEnd)
        val previousTotal = transactionRepository.getTotalExpenseByMonth(
            prevMonth, prevYear,
        )
        return combine(currentTotal, previousTotal) { current, prev ->
            val percentChange = if (prev == 0L) 0L else (((current - prev) / prev) * 100)
            ExpenseSummary(
                total = current,
                percentChangeFromLastMonth = percentChange.toFloat(),
            )
        }


    }
}