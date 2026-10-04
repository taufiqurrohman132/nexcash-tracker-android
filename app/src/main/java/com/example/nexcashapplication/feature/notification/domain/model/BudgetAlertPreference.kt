package com.example.nexcashapplication.feature.notification.domain.model

/**
 * Preferensi peringatan satu budget: apakah alert-nya aktif dan threshold persen mana
 * yang dipantau (mis. {80, 100}).
 *
 * @param budgetId id budget (sama dengan `Budget.id`); baris default (belum pernah diubah)
 *   boleh diwakili dengan id yang tidak ada di tabel — lihat repository.
 */
data class BudgetAlertPreference(
    val budgetId: String,
    val enabled: Boolean = true,
    val thresholds: Set<Int> = setOf(80, 100),
)
