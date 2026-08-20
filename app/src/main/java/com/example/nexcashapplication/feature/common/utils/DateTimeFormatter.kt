package com.example.nexcashapplication.feature.common.utils

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

private val dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

fun Instant.formatDate(zoneId: ZoneId = ZoneId.systemDefault()): String =
    this.toJavaInstant().atZone(zoneId).format(dateFormatter)

fun Instant.formatTime(zoneId: ZoneId = ZoneId.systemDefault()): String =
    this.toJavaInstant().atZone(zoneId).format(timeFormatter)

fun Instant.formatDateTime(zoneId: ZoneId = ZoneId.systemDefault()): String =
    "${this.formatDate(zoneId)} • ${this.formatTime(zoneId)}"

fun Instant.hour(zoneId: ZoneId = ZoneId.systemDefault()): Int =
    this.toJavaInstant().atZone(zoneId).hour

fun Instant.minute(zoneId: ZoneId = ZoneId.systemDefault()): Int =
    this.toJavaInstant().atZone(zoneId).minute

object DateTimeFormatter {
    fun timeToInstant(hour: Int, minute: Int, zoneId: ZoneId = ZoneId.systemDefault()): Instant =
        LocalDate
                .now()
                .atTime(hour, minute)
                .atZone(zoneId)
                .toInstant()
                .toKotlinInstant()

}
