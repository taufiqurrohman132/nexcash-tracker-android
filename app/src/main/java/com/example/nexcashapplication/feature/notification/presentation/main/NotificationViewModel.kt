package com.example.nexcashapplication.feature.notification.presentation.main

import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.feature.notification.domain.usecase.GetBudgetAlertsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Layar daftar notifikasi.
 *
 * Seluruh logika domain (combine repository, rollup subkategori, hitung % periode, mutasi alert
 * per threshold) ada di [GetBudgetAlertsUseCase]; ViewModel ini hanya state sesi presentation:
 * kapan alert pertama terlihat (untuk label waktu), mana yang sudah dibaca, lalu grouping per hari.
 */
@HiltViewModel
class NotificationViewModel @Inject constructor(
    getBudgetAlertsUseCase: GetBudgetAlertsUseCase,
) : BaseViewModel<NotificationState, NotificationEvent, NotificationEffect>(NotificationState()) {

    /**
     * Epoch millis alert pertama kali terlihat, kunci = id alert. In-memory: cukup supaya label
     * waktu tidak kembali ke "Baru saja" tiap kombinasi flow Room memancar ulang hasil yang sama.
     */
    private val firstSeenAt = mutableMapOf<String, Long>()

    /** Sumber kebenaran status baca — dipakai `combine` agar grup ikut recomputes saat menandai baca. */
    private val readIds = MutableStateFlow<Set<String>>(emptySet())

    init {
        observeAlerts(getBudgetAlertsUseCase)
    }

    override fun onEvent(event: NotificationEvent) {
        when (event) {
            NotificationEvent.BackClicked ->
                viewModelScope.launch { sendEffect(NotificationEffect.NavigateBack) }

            NotificationEvent.SettingsClicked ->
                viewModelScope.launch { sendEffect(NotificationEffect.NavigateToSettings) }

            is NotificationEvent.ItemClicked -> {
                markRead(event.id)
                viewModelScope.launch {
                    sendEffect(NotificationEffect.NavigateToBudget(event.budgetId))
                }
            }

            NotificationEvent.MarkAllReadClicked -> {
                val allIds = state.value.groups.flatMap { group -> group.items.map { it.id } }
                readIds.value = readIds.value + allIds
            }
        }
    }

    private fun markRead(id: String) {
        readIds.value = readIds.value + id
    }

    /** Alert domain + status baca → timestamp pertama kali terlihat → kelompokkan per hari. */
    private fun observeAlerts(getBudgetAlertsUseCase: GetBudgetAlertsUseCase) {
        combine(getBudgetAlertsUseCase(), readIds) { alerts, readIds -> alerts to readIds }
            // mapping + grouping format waktu kerja berat → background, jauh dari frame main
            .map { (alerts, read) ->
                val now = System.currentTimeMillis()
                val notificationAlerts = alerts.map { alert ->
                    // pertama kali terlihat → kunci waktunya; emisi berikutnya memakai waktu sama
                    alert.toNotificationAlert(firstSeenAt.getOrPut(alert.id) { now })
                }
                Triple(read, notificationAlerts, notificationAlerts.toGroupedUi(read, now))
            }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)
            .onEach { (read, alerts, groups) ->
                updateState {
                    it.copy(
                        groups = groups,
                        unreadCount = alerts.count { alert -> alert.id !in read },
                        readIds = read,
                        isLoading = false,
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
