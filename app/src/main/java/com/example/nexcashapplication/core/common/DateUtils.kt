package com.example.nexcashapplication.core.common

import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

fun getMonthlyPeriod(
    referenceDate: LocalDate,
    isRecurring: Boolean = false,
): Pair<LocalDate, LocalDate?> {
    val periodStart = referenceDate.withDayOfMonth(1)
    val periodEnd = referenceDate.withDayOfMonth(referenceDate.lengthOfMonth())
    val periodEndRecurring = if (isRecurring) null else periodEnd
    return periodStart to periodEndRecurring
}

fun getWeeklyPeriodByLocale(
    referenceDate: LocalDate,
    isRecurring: Boolean = false,
    locale: Locale = Locale.getDefault(),
): Pair<LocalDate, LocalDate?> {
    val weekFields = WeekFields.of(locale)
    val periodStart = referenceDate.with(weekFields.dayOfWeek(), 1)
    val periodEnd = periodStart.plusDays(6)
    val periodEndRecurring = if (isRecurring) null else periodEnd

    return periodStart to periodEndRecurring
}