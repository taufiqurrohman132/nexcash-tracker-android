package com.example.nexcashapplication.feature.account.domain.usecase

import com.example.nexcashapplication.feature.account.domain.model.MonthlyAccountSummary
import com.example.nexcashapplication.feature.account.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMonthlySummaryUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    operator fun invoke(): Flow<MonthlyAccountSummary> {
        return accountRepository.getMonthlySummary().map { result ->
            val netChange = result.monthlyIncome - result.monthlyExpense
            val startOfMonthlyBalance = result.totalBalance - netChange
            val percentage = if (startOfMonthlyBalance == 0L) 0.0
            else (netChange * 100.0) / startOfMonthlyBalance

            MonthlyAccountSummary(
                totalSaldoGabungan = result.totalBalance,
                percentage = percentage,
                activeAccount = result.activeAccount,
            )
        }
    }
}