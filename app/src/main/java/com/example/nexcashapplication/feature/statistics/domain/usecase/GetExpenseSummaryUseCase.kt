package com.example.nexcashapplication.feature.statistics.domain.usecase

import com.example.nexcashapplication.core.common.toEndOfMonthMillis
import com.example.nexcashapplication.core.common.toStartOfMonthMillis
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.statistics.domain.model.ExpenseSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.YearMonth
import javax.inject.Inject

/**
 * Ringkasan pengeluaran bulan [month] beserta tren persen terhadap bulan lalu.
 *
 * Perbaikan:
 * - Range bulan lalu dihitung dari [YearMonth.minusMonths]. Dulu memakai
 *   `previousMonthOf(monthStart, monthEnd)` yang parameternya (bulan, tahun)
 *   tapi diisi millis → "bulan lalu" nyaris sama dengan bulan ini → tren selalu ~0%.
 * - Pembagian wajib Float; versi Long `((current - previous) / previous) * 100`
 *   membuang pecahan (150 vs 100 hasilnya 0, bukan 50).
 */
class GetExpenseSummaryUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    /**
     * [currentTotalFlow] boleh dikirim dari luar supaya total pengeluaran
     * bulan berjalan tidak di-query dua kali (dipakai juga oleh
     * [GetBudgetHealthUseCase] di combine yang sama).
     */
    operator fun invoke(
        month: YearMonth,
        currentTotalFlow: Flow<Long> = transactionRepository.getTotalExpenseByMonth(
            month.toStartOfMonthMillis(),
            month.toEndOfMonthMillis(),
        ),
    ): Flow<ExpenseSummary> {
        val previousMonth = month.minusMonths(1)
        val previousTotal = transactionRepository.getTotalExpenseByMonth(
            previousMonth.toStartOfMonthMillis(),
            previousMonth.toEndOfMonthMillis(),
        )
        return combine(currentTotalFlow, previousTotal) { current, previous ->
            ExpenseSummary(
                total = current,
                percentChangeFromLastMonth = if (previous == 0L) {
                    0f
                } else {
                    (current - previous) * 100f / previous
                },
            )
        }
    }
}
