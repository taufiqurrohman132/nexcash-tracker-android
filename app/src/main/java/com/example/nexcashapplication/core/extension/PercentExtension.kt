package com.example.nexcashapplication.core.extension

import java.util.Locale

private val percentLocale = Locale("id", "ID")

/**
 * 35.67f → "35,67" — selalu 2 digit di belakang pemisah desimal.
 * Locale "id" biar konsisten dengan formatCurrency (pemisah koma).
 */
fun Float.formatDecimal2(): String =
    String.format(percentLocale, "%.2f", this)

/** 35.67f → "35,67%" */
fun Float.formatPercent(): String = "${formatDecimal2()}%"
