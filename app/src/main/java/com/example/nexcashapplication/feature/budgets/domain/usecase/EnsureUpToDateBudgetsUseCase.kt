package com.example.nexcashapplication.feature.budgets.domain.usecase

import com.example.nexcashapplication.core.common.getMonthlyPeriod
import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.repository.BudgetRepository
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * Lazy generation: saat aplikasi dibuka, pastikan setiap seri recurring punya
 * instance yang mencakup hari ini.
 *
 * Algoritme: untuk tiap root seri, ambil baris terakhir (start_date terbesar).
 * Selama baris terakhir berakhir sebelum hari ini, buat periode berikutnya:
 *   - start = end sebelumnya + 1 hari (tanpa gap/tumpang tindih)
 *   - end   = sesuai tipe periode (MONTHLY = kalender, WEEKLY = minggu locale,
 *             CUSTOM = durasi tetap root)
 * Amount disalin dari baris terakhir, sehingga edit limit di tengah periode
 * otomatis diwarisi periode berikutnya.
 *
 * Insert pakai IGNORE ((group_id, start_date) unik) → aman dijalankan berulang.
 * Iterasi dibatasi [MAX_PERIODS_PER_RUN] sebagai safety cap bila data sangat basi;
 * run berikutnya akan melanjutkan.
 */
class EnsureUpToDateBudgetsUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository,
) {
    suspend operator fun invoke(today: LocalDate = LocalDate.now()) {
        val roots = budgetRepository.getSeriesRoots()

        for (root in roots) {
            if (!root.isRecurring) continue

            val groupId = root.recurringGroupId ?: root.id ?: continue
            var last = budgetRepository.getLastBudgetInSeries(groupId) ?: root

            var generated = 0
            while (last.endDate.isBefore(today) && generated < MAX_PERIODS_PER_RUN) {
                val (start, end) = nextPeriod(last, root)
                if (!end.isAfter(start)) break // data tidak valid → hentikan, jangan loop

                val next = Budget(
                    categoryId = last.categoryId,
                    startDate = start,
                    endDate = end,
                    amount = last.amount,
                    recurrenceType = last.recurrenceType,
                    isRecurring = true,
                    recurringGroupId = groupId,
                )

                val inserted = budgetRepository.insertBudgetIfAbsent(next)
                if (!inserted) {
                    // Sudah ada di DB (run sebelumnya / run paralel) → lanjut dari baris nyata.
                    val refreshed = budgetRepository.getLastBudgetInSeries(groupId)
                    if (refreshed == null || !refreshed.endDate.isAfter(last.endDate)) break
                    last = refreshed
                } else {
                    last = next
                }
                generated++
            }
        }
    }

    /**
     * Periode berikutnya selalu start = end sebelumnya + 1 hari (tanpa gap/tumpang tindih),
     * end mengikuti tipe:
     * - MONTHLY : akhir bulan kalender dari start (bukan getMonthlyPeriod penuh, karena start
     *             bisa jatuh di tengah bulan saat periode sebelumnya terpotong)
     * - WEEKLY  : start + 6 hari (anchor end+1; penyelarasan locale bisa Geser start mundur
     *             sehingga tumpang tindih dengan periode sebelumnya)
     * - CUSTOM  : durasi tetap root (end - start)
     */
    private fun nextPeriod(last: Budget, root: Budget): Pair<LocalDate, LocalDate> {
        val start = last.endDate.plusDays(1)
        return when (last.recurrenceType) {
            RecurrenceType.MONTHLY -> start to getMonthlyPeriod(start).second
            RecurrenceType.WEEKLY -> start to start.plusDays(6)
            RecurrenceType.CUSTOM -> {
                val interval = ChronoUnit.DAYS.between(root.startDate, root.endDate)
                start to start.plusDays(interval)
            }
        }
    }

    private companion object {
        const val MAX_PERIODS_PER_RUN = 24
    }
}
