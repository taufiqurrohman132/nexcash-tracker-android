package com.example.nexcashapplication.feature.notification.presentation.edit

/**
 * Threshold persen yang bisa dipilih per anggaran.
 * Dipakai `AlertThreshold.all` untuk merender chip di layar pengaturan.
 */
object AlertThreshold {
    val all: List<Int> = listOf(80, 100)
}

/**
 * Satu kartu pengaturan peringatan anggaran.
 *
 * Sumbernya Room (`notification_preferences`) lewat [NotificationPreferenceRepository] —
 * preferensi bertahan lintas sesi.
 */
data class BudgetAlertUi(
    val budgetId: String,
    val name: String,
    val iconKey: String,
    val enabled: Boolean = true,
    /** Default sama dengan `BudgetAlertPreference` — on, 80% & 100%. */
    val thresholds: Set<Int> = setOf(80, 100),
)
