package com.example.nexcashapplication.core.extension

import com.example.nexcashapplication.core.currency.AppCurrencyHolder
import com.example.nexcashapplication.core.i18n.LocaleCached
import java.text.NumberFormat
import java.util.Locale

/**
 * Pemisah ribuan/desimal mengikuti bahasa aplikasi saat ini (1.000 di id, 1,000 di en),
 * bukan di-pin ke locale Indonesia. Di-cache per-locale karena bahasa bisa berganti saat
 * runtime — lihat [LocaleCached].
 *
 * Simbol mata uang ("Rp", "$", ...) ikut pilihan di pengaturan Mata Uang lewat
 * [AppCurrencyHolder] — jadi format di sini sudah termasuk prefix siap tampil;
 * pemanggil tidak perlu menambah "Rp" sendiri (baik lewat kode maupun resource).
 * `parseCurrency`/`parseCurrencyCompact` tetap aman karena membuang semua non-digit.
 */
private val numberFormatter = LocaleCached { locale: Locale -> NumberFormat.getNumberInstance(locale) }

fun Long.formatCurrency(): String {
    return AppCurrencyHolder.value.prefix + numberFormatter.get().format(this)
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
    val compact = when {
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
    return AppCurrencyHolder.value.prefix + compact
}

private fun formatDecimal(value: Double): String {
    // Pemisah desimal mengikuti bahasa aplikasi; parseCurrencyCompact sudah menampung
    // pemisah koma maupun titik.
    return String.format(Locale.getDefault(), "%.2f", value)
            .trimEnd('0')  // hapus 0 di belakang → 1.50 jadi 1.5
            .trimEnd('.')  // hapus titik kalau tidak ada desimal → 1. jadi 1
}


// visibilty
fun Long.toFormatCurrencyVisibility(isVisible: Boolean, isTotalSaldoOverflow: Boolean = false): String {
    return  if (!isVisible) {
        AppCurrencyHolder.value.prefix + "••••••••"
    } else if (isTotalSaldoOverflow) {
        this.formatCompact()
    } else {
        this.formatCurrency()
    }
}