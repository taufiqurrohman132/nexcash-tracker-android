package com.example.nexcashapplication.feature.notification

import com.example.nexcashapplication.feature.notification.domain.model.BudgetAlertPreference
import com.example.nexcashapplication.feature.notification.domain.repository.NotificationPreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeNotificationPreferenceRepository : NotificationPreferenceRepository {

    private val _preferences = MutableStateFlow<Map<String, BudgetAlertPreference>>(emptyMap())

    override val preferences: Flow<List<BudgetAlertPreference>> =
        _preferences.map { it.values.toList() }

    override fun observeForBudget(budgetId: String): Flow<BudgetAlertPreference?> =
        _preferences.map { it[budgetId] }

    override suspend fun setEnabled(budgetId: String, enabled: Boolean) {
        val current = _preferences.value[budgetId] ?: BudgetAlertPreference(budgetId)
        _preferences.value = _preferences.value + (budgetId to current.copy(enabled = enabled))
    }

    override suspend fun setThresholds(budgetId: String, thresholds: Set<Int>) {
        val current = _preferences.value[budgetId] ?: BudgetAlertPreference(budgetId)
        _preferences.value = _preferences.value + (budgetId to current.copy(thresholds = thresholds))
    }

    fun seed(vararg prefs: BudgetAlertPreference) {
        _preferences.value = _preferences.value + prefs.associateBy { it.budgetId }
    }
}
