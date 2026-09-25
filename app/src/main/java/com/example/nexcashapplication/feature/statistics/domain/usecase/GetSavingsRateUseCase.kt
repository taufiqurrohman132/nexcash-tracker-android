package com.example.nexcashapplication.feature.statistics.domain.usecase

import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.statistics.domain.model.SavingsRate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.YearMonth
import javax.inject.Inject

/**
 * Tingkat tabungan bulan [month] = (pemasukan − pengeluaran) / pemasukan × 100,
 * plus tren poin persen terhadap bulan lalu.
 *
 * Memakai [TransactionRepository.getMonthlySummary] per bulan (`"yyyy-MM"`,
 * sama seperti dashboard) sehingga tidak butuh query income baru. Pemasukan ≤ 0
 * → rate 0; bulan lalu tanpa pemasukan → tren 0 (tidak ada pembanding yang jujur).
 */
class GetSavingsRateUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(month: YearMonth): Flow<SavingsRate> {
        val previousMonth = month.minusMonths(1)
        val currentSummary = transactionRepository.getMonthlySummary(month.toString())
        val previousSummary = transactionRepository.getMonthlySummary(previousMonth.toString())
        return combine(currentSummary, previousSummary) { current, previous ->
            val currentRate = rateOf(current.totalPemasukan, current.totalPengeluaran)
            SavingsRate(
                rate = currentRate,
                trendPercent = if (previous.totalPemasukan <= 0L) {
                    0f
                } else {
                    currentRate - rateOf(previous.totalPemasukan, previous.totalPengeluaran)
                },
            )
        }
    }

    private fun rateOf(income: Long, expense: Long): Float =
        if (income <= 0L) 0f else (income - expense) * 100f / income
}
