package com.example.nexcashapplication.feature.notification.presentation.edit

sealed interface NotificationSettingsEvent {
    data object BackClicked : NotificationSettingsEvent

    /** Saklar master "Aktifkan pengingat". */
    data class MasterToggled(val enabled: Boolean) : NotificationSettingsEvent

    /** Saklar peringatan satu budget. */
    data class BudgetToggled(val budgetId: String, val enabled: Boolean) : NotificationSettingsEvent

    /** Chip threshold (80% / 100%) satu budget ditekan. */
    data class ThresholdToggled(
        val budgetId: String,
        val percent: Int,
        val selected: Boolean,
    ) : NotificationSettingsEvent

    /** Hasil cek ulang izin (dipanggil saat ON_RESUME). */
    data class PermissionChecked(val granted: Boolean) : NotificationSettingsEvent

    /** Hasil dialog izin POST_NOTIFICATIONS. */
    data class PermissionRequestResult(val granted: Boolean) : NotificationSettingsEvent
}
