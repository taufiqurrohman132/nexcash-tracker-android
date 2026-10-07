package com.example.nexcashapplication.core.extension

import java.util.Locale

/**
 * 35.67f → "35,67" — selalu 2 digit di belakang pemisah desimal.
 *
 * Pemisah mengikuti bahasa aplikasi (konsisten dengan `formatCurrency`): `Locale.getDefault()`
 * memang sengaja dipakai tiap pemanggilan — bahasa bisa berubah saat runtime (ganti bahasa di
 * Settings → `Activity.recreate()`), jadi locale tidak boleh di-cache di top-level val.
 */
fun Float.formatDecimal2(): String =
    String.format(Locale.getDefault(), "%.2f", this)

/** 35.67f → "35,67%" — pemisah desimal mengikuti bahasa aplikasi. */
fun Float.formatPercent(): String = "${formatDecimal2()}%"
