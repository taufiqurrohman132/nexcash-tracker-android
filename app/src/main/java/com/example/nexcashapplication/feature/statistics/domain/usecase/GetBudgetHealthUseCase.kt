package com.example.nexcashapplication.feature.statistics.domain.usecase

import com.example.nexcashapplication.core.common.toEndOfMonthMillis
import com.example.nexcashapplication.core.common.toStartOfMonthMillis
import com.example.nexcashapplication.core.domain.repository.BudgetRepository
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.statistics.domain.model.BudgetHealth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.YearMonth
import javax.inject.Inject

/**
 * Sisa anggaran bulan [month]: total budget yang menabrak periode tersebut
 * dikurangi pengeluaran aktual, plus persentase terpakai.
 *
 * Sumber data sama dengan mode ALOKASI di [GetCategoryAllocationUseCase]
 * ([BudgetRepository.getBudgetsByMonth]) supaya angka saling konsisten.
 */
class GetBudgetHealthUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(month: YearMonth): Flow<BudgetHealth> {
        val monthStart = month.toStartOfMonthMillis()
        val monthEnd = month.toEndOfMonthMillis()
        return combine(
            budgetRepository.getBudgetsByMonth(monthStart, monthEnd),
            transactionRepository.getTotalExpenseByMonth(monthStart, monthEnd),
        ) { budgets, expense ->
            val total = budgets.sumOf { it.amount }
            BudgetHealth(
                totalBudget = total,
                sisa = total - expense,
                usedPercent = if (total > 0L) expense * 100f / total else 0f,
            )
        }
    }
}
