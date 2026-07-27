package com.example.nexcashapplication.core.extension

import java.text.NumberFormat
import java.util.Locale

private val currencyFormatter = NumberFormat
        .getNumberInstance(Locale("id", "ID"))

fun Long.formatCurrency(): String {
    return currencyFormatter.format(this)
}

// Untuk currency formatted string
fun String.parseCurrency(): Long {
    return this.replace("[^\\d]".toRegex(), "").toLongOrNull() ?: 0L
}

// Untuk compact formatted string
fun String.parseCurrencyCompact(): Long {
    return try {
        val cleaned = this.trim().uppercase()
        when {
            cleaned.endsWith("T") -> (cleaned.removeSuffix("T").replace(",", ".")
                    .toDouble() * 1_000_000_000_000).toLong()

            cleaned.endsWith("M") -> (cleaned.removeSuffix("M").replace(",", ".")
                    .toDouble() * 1_000_000_000).toLong()

            cleaned.endsWith("JT") -> (cleaned.removeSuffix("JT").replace(",", ".")
                    .toDouble() * 1_000_000).toLong()

            cleaned.endsWith("K") -> (cleaned.removeSuffix("K").replace(",", ".")
                    .toDouble() * 1_000).toLong()

            else -> this.replace("[^\\d]".toRegex(), "").toLongOrNull() ?: 0L
        }
    } catch (e: Exception) {
        0L
    }
}

fun Long.formatCompact(): String {
    return when {
        this >= 1_000_000_000_000 -> {
            val value = this / 1_000_000_000_000.0
            "${formatDecimal(value)}T"
        }

        this >= 1_000_000_000 -> {
            val value = this / 1_000_000_000.0
            "${formatDecimal(value)}M"
        }

        this >= 1_000_000 -> {
            val value = this / 1_000_000.0
            "${formatDecimal(value)}Jt"
        }

        this >= 1_000 -> {
            val value = this / 1_000.0
            "${formatDecimal(value)}K"
        }

        else -> this.toString()
    }
}

private fun formatDecimal(value: Double): String {
    return String.format(Locale("id", "ID"), "%.2f", value)
            .trimEnd('0')  // hapus 0 di belakang → 1.50 jadi 1.5
            .trimEnd('.')  // hapus titik kalau tidak ada desimal → 1. jadi 1
}