package com.example.nexcashapplication.core.i18n

import java.util.Locale

/**
 * Cache formatter per-locale.
 *
 * Formatter (`NumberFormat`, `DateTimeFormatter`) menempel pada locale saat dibuat, sedangkan
 * bahasa bisa berubah saat runtime (ganti bahasa → `Activity.recreate()`). Dengan cache begini,
 * begitu [Locale.getDefault()] berubah, formatter lama dibuang dan dibuat ulang — tanpa harus
 * membuat formatter baru di tiap pemanggilan (penting karena dipakai di dalam list/LazyColumn).
 *
 * ```kotlin
 * private val currencyFormatter = LocaleCached { NumberFormat.getNumberInstance(it) }
 * fun Long.formatCurrency(): String = currencyFormatter.get().format(this)
 * ```
 */
class LocaleCached<T : Any>(private val create: (Locale) -> T) {

    @Volatile
    private var locale: Locale? = null

    @Volatile
    private var value: T? = null

    /** Formatter untuk [Locale.getDefault] saat ini; dibuat ulang kalau locale berubah. */
    fun get(): T {
        val current = Locale.getDefault()
        if (value == null || locale != current) {
            value = create(current)
            locale = current
        }
        return checkNotNull(value)
    }
}
