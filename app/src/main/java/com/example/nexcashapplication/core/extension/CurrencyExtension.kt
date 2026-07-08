package com.example.nexcashapplication.core.extension

import java.text.NumberFormat
import java.util.Locale

private val currencyFormatter = NumberFormat
    .getNumberInstance(Locale("id", "ID"))

fun Long.formatCurrency(): String {
    return currencyFormatter.format(this)
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