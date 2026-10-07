package com.example.nexcashapplication.core.domain.model

import androidx.annotation.StringRes
import com.example.nexcashapplication.R

/**
 * Tipe kategori.
 *
 * [labelResId] adalah kunci string resource untuk teks tampilan — resolve di UI dengan
 * `stringResource(tipe.labelResId)`, jangan ubah jadi `String` (biar ikut bahasa aktif).
 * `name.lowercase()` tetap dipakai untuk keperluan persistensi/query, bukan tampilan.
 */
enum class TypeCategory(@StringRes val labelResId: Int) {
    EXPENSE(R.string.cat_type_expense),
    INCOME(R.string.cat_type_income),
}
