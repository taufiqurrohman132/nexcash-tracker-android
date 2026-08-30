package com.example.nexcashapplication.core.common

import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant
import java.time.LocalDate
import java.time.ZoneId

fun Long.toLocalDate(zoneId: ZoneId = ZoneId.systemDefault()): LocalDate =
    Instant.fromEpochMilliseconds(this)
            .toJavaInstant()          // <- convert dulu ke java.time.Instant
            .atZone(zoneId)
            .toLocalDate()

fun LocalDate.toEpochMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long =
    this.atStartOfDay(zoneId)
            .toInstant()               // ini java.time.Instant
            .toKotlinInstant()         // <- convert ke kotlin.time.Instant
            .toEpochMilliseconds()