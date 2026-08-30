package com.example.nexcashapplication.feature.budgets.domain.usecase

import com.example.nexcashapplication.core.common.getMonthlyPeriod
import com.example.nexcashapplication.core.common.getWeeklyPeriodByLocale
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import java.time.LocalDate
import javax.inject.Inject

class GetBudgetPeriodUseCase @Inject constructor() {
    operator fun invoke(
        isRecurring: Boolean,
        type: RecurrenceType, // WEEKLY / MONTHLY
        startDate: LocalDate,
        endDate: LocalDate?,
    ): Pair<LocalDate, LocalDate?> {
        return when (type) {
            RecurrenceType.WEEKLY -> getWeeklyPeriodByLocale(startDate)
            RecurrenceType.MONTHLY -> getMonthlyPeriod(startDate)
            RecurrenceType.CUSTOM -> startDate to endDate
        }
    }
}