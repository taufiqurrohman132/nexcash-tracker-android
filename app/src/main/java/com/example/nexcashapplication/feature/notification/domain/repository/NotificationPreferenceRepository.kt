package com.example.nexcashapplication.feature.notification.domain.repository

import com.example.nexcashapplication.feature.notification.domain.model.BudgetAlertPreference
import kotlinx.coroutines.flow.Flow

/**
 * Preferensi peringatan notifikasi per budget, tersimpan di Room
 * (tabel `notification_preferences`).
 *
 * Master switch tidak di sini — itu milik `SettingsRepository` (DataStore).
 */
interface NotificationPreferenceRepository {

    /** Semua preferensi yang pernah disetel. Budget tanpa baris = default (on, {80, 100}). */
    val preferences: Flow<List<BudgetAlertPreference>>

    /** Preferensi satu budget, atau `null` kalau belum pernah disetel (berarti default). */
    fun observeForBudget(budgetId: String): Flow<BudgetAlertPreference?>

    suspend fun setEnabled(budgetId: String, enabled: Boolean)

    /** Ganti seluruh threshold aktif budget ini. */
    suspend fun setThresholds(budgetId: String, thresholds: Set<Int>)
}
