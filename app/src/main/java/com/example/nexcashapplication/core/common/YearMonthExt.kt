package com.example.nexcashapplication.core.common

import java.time.Instant
import java.time.YearMonth
import java.time.ZoneOffset

val data = Instant.now().toEpochMilli()

fun YearMonth.toStartOfMonthMillis(): Long {
    return this.atDay(1)
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()
}

fun YearMonth.toEndOfMonthMillis(): Long {
    return this.atEndOfMonth()
            .atTime(23, 59, 59, 999_000_000)
            .toInstant(ZoneOffset.UTC) // ← sama-sama UTC
            .toEpochMilli()
}