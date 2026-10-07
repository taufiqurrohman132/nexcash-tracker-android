package com.example.nexcashapplication.feature.budgets.domain.model

import androidx.annotation.StringRes
import com.example.nexcashapplication.R

/**
 * Tipe periode anggaran.
 *
 * [labelResId] adalah kunci string resource untuk teks tampilan — resolve di UI dengan
 * `stringResource(type.labelResId)`. [name], [toString], dan pemakaian persistensi
 * (`BudgetMapper` / `valueOf`) TIDAK berubah; property ini hanya menambah label terjemahan.
 */
enum class RecurrenceType(@StringRes val labelResId: Int) {
    CUSTOM(R.string.budget_recurrence_custom),
    WEEKLY(R.string.budget_recurrence_weekly),
    MONTHLY(R.string.budget_recurrence_monthly),
}
