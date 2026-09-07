package com.example.nexcashapplication.core.common

import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

fun getMonthlyPeriod(referenceDate: LocalDate): Pair<LocalDate, LocalDate> {
    val periodStart = referenceDate.withDayOfMonth(1)
    val periodEnd = referenceDate.withDayOfMonth(referenceDate.lengthOfMonth())
    return periodStart to periodEnd
}

fun getWeeklyPeriodByLocale(
    referenceDate: LocalDate,
    locale: Locale = Locale.getDefault(),
): Pair<LocalDate, LocalDate> {
    val weekFields = WeekFields.of(locale)
    val periodStart = referenceDate.with(weekFields.dayOfWeek(), 1)
    val periodEnd = periodStart.plusDays(6)
    return periodStart to periodEnd
}

fun previousMonthOf(month: Long, year: Long): Pair<Long, Long> {
    return if (month == 1L) 12L to (year - 1L) else (month - 1L) to year
}