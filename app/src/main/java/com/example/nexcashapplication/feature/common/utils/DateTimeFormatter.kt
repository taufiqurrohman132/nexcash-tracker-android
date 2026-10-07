package com.example.nexcashapplication.feature.common.utils

import com.example.nexcashapplication.core.i18n.LocaleCached
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Locale
import kotlin.time.Instant
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant
import java.time.format.DateTimeFormatter as JavaDateTimeFormatter

// Nama bulan dalam pola "d MMM yyyy" mengikuti bahasa aplikasi (Apr / Apr / avr ...).
// Di-cache per-locale karena bahasa bisa berganti saat runtime — lihat LocaleCached.
private val dateFormatter = LocaleCached { locale: Locale ->
    JavaDateTimeFormatter.ofPattern("d MMM yyyy", locale)
}
private val timeFormatter = LocaleCached { locale: Locale ->
    JavaDateTimeFormatter.ofPattern("HH:mm", locale)
}

// satu titik konversi, dipakai semua fungsi Instant di bawah
private fun Instant.toZoned(zoneId: ZoneId): ZonedDateTime =
    this.toJavaInstant().atZone(zoneId)

fun Instant.formatDate(zoneId: ZoneId = ZoneId.systemDefault()): String =
    this.toZoned(zoneId).format(dateFormatter.get())

fun Instant.formatTime(zoneId: ZoneId = ZoneId.systemDefault()): String =
    this.toZoned(zoneId).format(timeFormatter.get())

fun Instant.formatDateTime(zoneId: ZoneId = ZoneId.systemDefault()): String =
    "${formatDate(zoneId)} • ${formatTime(zoneId)}"

fun Instant.hour(zoneId: ZoneId = ZoneId.systemDefault()): Int =
    this.toZoned(zoneId).hour

fun Instant.minute(zoneId: ZoneId = ZoneId.systemDefault()): Int =
    this.toZoned(zoneId).minute

// LocalDate gak butuh zoneId sama sekali
fun LocalDate.formatDate(): String =
    this.toJavaLocalDate().format(dateFormatter.get())

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

// ---- DatePicker & TimePicker ----
// DatePicker Material3 selalu bekerja di "UTC midnight" dari tanggal yang dipilih,
// bukan Instant bebas — kalau dikirim mentah (mis. 15 Apr 09:00 lokal = 14 Apr 23:00 UTC)
// tanggalnya bisa meleset sehari. Titik konversinya cuma di sini.

/** Tanggal lokal dari [this] -> millis UTC midnight untuk `AppDatePickerDialog.initialDateMillis`. */
fun Instant.toDatePickerMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long =
    this.toZoned(zoneId).toLocalDate()
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()

/** Millis hasil DatePicker -> tanggal lokal (dibaca sebagai UTC, sesuai konvensi Material3). */
fun Long.toPickedLocalDate(): java.time.LocalDate =
    java.time.Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

/**
 * Ganti tanggal [this] dengan [date], **jam/menit lama dipertahankan**.
 * Dipakai OnDateChange supaya memilih tanggal tidak mereset jam yang sudah dipilih.
 */
fun Instant.withDate(
    date: java.time.LocalDate,
    zoneId: ZoneId = ZoneId.systemDefault(),
): Instant =
    date.atTime(this.toZoned(zoneId).toLocalTime())
        .atZone(zoneId)
        .toInstant()
        .toKotlinInstant()

/**
 * Ganti jam [this] dengan [hour]:[minute], **tanggal lama dipertahankan**.
 * Dipakai OnTimeChange supaya memilih jam tidak mereset tanggal ke hari ini.
 */
fun Instant.withTime(
    hour: Int,
    minute: Int,
    zoneId: ZoneId = ZoneId.systemDefault(),
): Instant =
    this.toZoned(zoneId).toLocalDate()
        .atTime(hour, minute)
        .atZone(zoneId)
        .toInstant()
        .toKotlinInstant()