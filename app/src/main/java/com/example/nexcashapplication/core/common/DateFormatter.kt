package com.example.nexcashapplication.core.common

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun Long.toLocalDate(zoneId: ZoneId = ZoneId.systemDefault()): LocalDate =
    Instant.ofEpochMilli(this).atZone(zoneId).toLocalDate()


// LocalDate -> Long
fun LocalDate.toEpochMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long =
    this.atStartOfDay(zoneId).toInstant().toEpochMilli()