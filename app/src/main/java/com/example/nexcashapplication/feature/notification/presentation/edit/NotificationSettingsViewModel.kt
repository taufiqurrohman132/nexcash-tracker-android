package com.example.nexcashapplication.feature.notification.presentation.edit

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.common.toEpochMillis
import com.example.nexcashapplication.core.domain.repository.BudgetRepository
import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.core.i18n.toUiText
import com.example.nexcashapplication.feature.notification.domain.model.BudgetAlertPreference
import com.example.nexcashapplication.feature.notification.domain.repository.NotificationPreferenceRepository
import com.example.nexcashapplication.feature.settings.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

private const val TAG = "NotifSettingsVm"

/**
 * Pengaturan notifikasi: daftar budget aktif dari Room, preferensi dari
 * [NotificationPreferenceRepository] (tabel `notification_preferences`), saklar master dari
 * [SettingsRepository] (DataStore). Semua tersimpan — bukan in-memory lagi.
 *
 * Izin POST_NOTIFICATIONS sengaja in-memory: itu status sistem, bukan data aplikasi.
 */
@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val notificationPreferenceRepository: NotificationPreferenceRepository,
    private val settingsRepository: SettingsRepository,
) : BaseViewModel<NotificationSettingsState, NotificationSettingsEvent, NotificationSettingsEffect>(
    NotificationSettingsState(),
) {

    init {
        observeMaster()
        observeBudgets()
    }

    override fun onEvent(event: NotificationSettingsEvent) {
        when (event) {
            NotificationSettingsEvent.BackClicked ->
                viewModelScope.launch { sendEffect(NotificationSettingsEffect.NavigateBack) }

            is NotificationSettingsEvent.MasterToggled -> {
                updateState {
                    it.copy(masterEnabled = event.enabled, remindersActive = event.enabled)
                }
                viewModelScope.launch {
                    settingsRepository.updateNotificationEnabled(event.enabled)
                }
                // nyalakan tapi izin belum ada → langsung minta
                if (event.enabled && !state.value.permissionGranted) {
                    viewModelScope.launch {
                        sendEffect(NotificationSettingsEffect.RequestNotificationPermission)
                    }
                }
            }

            is NotificationSettingsEvent.BudgetToggled ->
                viewModelScope.launch {
                    notificationPreferenceRepository.setEnabled(event.budgetId, event.enabled)
                }

            is NotificationSettingsEvent.ThresholdToggled ->
                viewModelScope.launch {
                    // daftar kartu sudah mencerminkan pref tersimpan → pakai itu sebagai dasar
                    val current = state.value.budgets
                            .firstOrNull { it.budgetId == event.budgetId }
                            ?.thresholds
                            ?: BudgetAlertPreference(event.budgetId).thresholds
                    val thresholds = if (event.selected) {
                        current + event.percent
                    } else {
                        current - event.percent
                    }
                    notificationPreferenceRepository.setThresholds(event.budgetId, thresholds)
                }

            is NotificationSettingsEvent.PermissionChecked ->
                updateState { it.copy(permissionGranted = event.granted) }

            is NotificationSettingsEvent.PermissionRequestResult ->
                updateState { it.copy(permissionGranted = event.granted) }
        }
    }

    private fun observeMaster() {
        settingsRepository.settings
                .map { it.notificationMasterEnabled }
                .distinctUntilChanged()
                .onEach { enabled ->
                    updateState {
                        it.copy(masterEnabled = enabled, remindersActive = enabled)
                    }
                }
                // DataStore gagal baca → jangan crash; saklar tetap di nilai default
                // state (false) dan layar tetap utuh.
                .catch { e ->
                    Log.e(TAG, "gagal membaca preferensi master notifikasi", e)
                }
                .launchIn(viewModelScope)
    }

    /** Budget expense aktif + preferensinya → kartu pengaturan. */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeBudgets() {
        categoryRepository.getExpenseCategories()
                .flatMapLatest { categories ->
                    val ids = categories.map { it.id }
                    val budgetsFlow = if (ids.isEmpty()) {
                        flowOf(emptyList())
                    } else {
                        budgetRepository.getActiveBudgetsForCategories(
                            categoryIds = ids,
                            referenceDate = LocalDate.now().toEpochMillis(),
                        )
                    }
                    combine(
                        budgetsFlow,
                        notificationPreferenceRepository.preferences,
                    ) { budgets, preferences -> Triple(categories, budgets, preferences) }
                }
                .distinctUntilChanged()
                .onEach { (categories, budgets, preferences) ->
                    val nameById = categories.associate { it.id to it.name }
                    val iconById = categories.associate { it.id to it.iconId }
                    val prefByBudget = preferences.associateBy { it.budgetId }

                    val items = budgets.mapNotNull { budget ->
                        val id = budget.id ?: return@mapNotNull null
                        val pref = prefByBudget[id] ?: BudgetAlertPreference(id)
                        BudgetAlertUi(
                            budgetId = id,
                            name = nameById[budget.categoryId] ?: budget.categoryId,
                            iconKey = iconById[budget.categoryId] ?: "category",
                            enabled = pref.enabled,
                            thresholds = pref.thresholds,
                        )
                    }

                    updateState {
                        it.copy(budgets = items, isLoading = false, error = null)
                    }
                }
                // Wajib: tanpa catch kegagalan Room bocor ke viewModelScope = crash.
                // Gagal baca menghentikan indikator dan mengisi error; daftar lama
                // (kalau ada) tidak dibuang supaya layar tetap menampilkan kartu tervalid.
                .catch { e ->
                    Log.e(TAG, "gagal memuat budget untuk pengaturan notifikasi", e)
                    updateState {
                        it.copy(
                            isLoading = false,
                            error = e.toUiText(R.string.budget_load_failed),
                        )
                    }
                }
                .launchIn(viewModelScope)
    }
}
