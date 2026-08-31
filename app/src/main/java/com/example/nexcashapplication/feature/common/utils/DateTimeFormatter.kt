package com.example.nexcashapplication.feature.common.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant
import java.time.format.DateTimeFormatter as JavaDateTimeFormatter

private val dateFormatter: JavaDateTimeFormatter = JavaDateTimeFormatter.ofPattern("d MMM yyyy")
private val timeFormatter: JavaDateTimeFormatter = JavaDateTimeFormatter.ofPattern("HH:mm")

// satu titik konversi, dipakai semua fungsi Instant di bawah
private fun Instant.toZoned(zoneId: ZoneId): ZonedDateTime =
    this.toJavaInstant().atZone(zoneId)

fun Instant.formatDate(zoneId: ZoneId = ZoneId.systemDefault()): String =
    this.toZoned(zoneId).format(dateFormatter)

fun Instant.formatTime(zoneId: ZoneId = ZoneId.systemDefault()): String =
    this.toZoned(zoneId).format(timeFormatter)

fun Instant.formatDateTime(zoneId: ZoneId = ZoneId.systemDefault()): String =
    "${formatDate(zoneId)} • ${formatTime(zoneId)}"

fun Instant.hour(zoneId: ZoneId = ZoneId.systemDefault()): Int =
    this.toZoned(zoneId).hour

fun Instant.minute(zoneId: ZoneId = ZoneId.systemDefault()): Int =
    this.toZoned(zoneId).minute

// LocalDate gak butuh zoneId sama sekali
fun LocalDate.formatDate(): String =
    this.toJavaLocalDate().format(dateFormatter)

fun java.time.LocalDate.toStartOfDayMillis(zone: ZoneId = ZoneId.systemDefault()): Long =
    atStartOfDay(zone).toInstant().toEpochMilli()

fun java.time.LocalDate.toEndOfDayMillis(zone: ZoneId = ZoneId.systemDefault()): Long =
    atTime(23, 59, 59, 999_999_999).atZone(zone).toInstant().toEpochMilli()

// rename dari object DateTimeFormatter -> hindari tabrakan nama sama java.time.format.DateTimeFormatter
object TimeParser {
    fun timeToInstant(hour: Int, minute: Int, zoneId: ZoneId = ZoneId.systemDefault()): Instant =
        java.time.LocalDate.now()
                .atTime(hour, minute)
                .atZone(zoneId)
                .toInstant()
                .toKotlinInstant()
}