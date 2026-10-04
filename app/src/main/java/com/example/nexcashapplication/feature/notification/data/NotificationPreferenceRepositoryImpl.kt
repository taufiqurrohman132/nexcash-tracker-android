package com.example.nexcashapplication.feature.notification.data

import com.example.nexcashapplication.core.datasource.local.dao.NotificationPreferenceDao
import com.example.nexcashapplication.core.datasource.local.model.NotificationPreferenceEntity
import com.example.nexcashapplication.feature.notification.domain.model.BudgetAlertPreference
import com.example.nexcashapplication.feature.notification.domain.repository.NotificationPreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * [NotificationPreferenceDao] ↔ domain. Satu baris = satu budget, primary key = budgetId
 * (lihat [NotificationPreferenceEntity]) sehingga upsert cukup `REPLACE`.
 *
 * Budget yang belum pernah disetel tidak punya baris → `null`/default ditangani pemanggil,
 * repository tidak mengarang baris saat dibaca.
 */
class NotificationPreferenceRepositoryImpl @Inject constructor(
    private val dao: NotificationPreferenceDao,
) : NotificationPreferenceRepository {

    override val preferences: Flow<List<BudgetAlertPreference>> =
        dao.observeAll().map { rows -> rows.map { it.toDomain() } }

    override fun observeForBudget(budgetId: String): Flow<BudgetAlertPreference?> =
        dao.observeAll().map { rows -> rows.firstOrNull { it.budgetId == budgetId }?.toDomain() }

    override suspend fun setEnabled(budgetId: String, enabled: Boolean) {
        dao.upsert(currentFor(budgetId).copy(enabled = enabled).toEntity(budgetId))
    }

    override suspend fun setThresholds(budgetId: String, thresholds: Set<Int>) {
        dao.upsert(currentFor(budgetId).copy(thresholds = thresholds).toEntity(budgetId))
    }

    /** Baris lama sebagai dasar update; belum ada → default (on, 80 & 100). */
    private suspend fun currentFor(budgetId: String): BudgetAlertPreference =
        dao.observeAll().first().firstOrNull { it.budgetId == budgetId }?.toDomain()
            ?: BudgetAlertPreference(budgetId = budgetId)

    private fun NotificationPreferenceEntity.toDomain() = BudgetAlertPreference(
        budgetId = budgetId,
        enabled = enabled,
        thresholds = thresholds.split(',')
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet(),
    )

    private fun BudgetAlertPreference.toEntity(budgetId: String) = NotificationPreferenceEntity(
        id = budgetId,
        budgetId = budgetId,
        thresholds = thresholds.joinToString(","),
        enabled = enabled,
    )
}
