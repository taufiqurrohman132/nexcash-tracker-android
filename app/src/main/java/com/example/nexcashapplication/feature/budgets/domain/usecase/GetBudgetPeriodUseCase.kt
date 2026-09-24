package com.example.nexcashapplication.feature.budgets.domain.usecase

import com.example.nexcashapplication.core.common.getMonthlyPeriod
import com.example.nexcashapplication.core.common.getWeeklyPeriodByLocale
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import java.time.LocalDate
import javax.inject.Inject

/**
 * Menghitung window (start..end) periode anggaran berdasarkan tipe periode.
 *
 * - WEEKLY  : minggu locale (hari pertama minggu menurut [java.util.Locale.getDefault]) .. +6 hari.
 * - MONTHLY : 1 .. akhir bulan kalender dari [startDate].
 * - CUSTOM  : [startDate] .. [endDate] (wajib; error bila endDate null).
 *
 * Dipakai saat create/ganti tipe (anchor pada hari ini) dan saat form menampilkan
 * pratinjau periode. Untuk generate periode berikutnya pada seri berjalan,
 * lihat [EnsureUpToDateBudgetsUseCase] (start = end sebelumnya + 1 hari).
 */
class GetBudgetPeriodUseCase @Inject constructor() {
    operator fun invoke(
        isRecurring: Boolean,
        type: RecurrenceType, // WEEKLY / MONTHLY
        startDate: LocalDate,
        endDate: LocalDate?,
    ): Pair<LocalDate, LocalDate> {
        return when (type) {
            RecurrenceType.WEEKLY -> getWeeklyPeriodByLocale(startDate)
            RecurrenceType.MONTHLY -> getMonthlyPeriod(startDate)
            RecurrenceType.CUSTOM -> {
                val periodEnd = endDate
                    ?: error("CUSTOM budget must have currentPeriodEnd")
                startDate to periodEnd
            }

        }
    }
}