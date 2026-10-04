package com.example.nexcashapplication.feature.notification.presentation.edit

sealed interface NotificationSettingsEffect {
    data object NavigateBack : NotificationSettingsEffect

    /** Minta izin POST_NOTIFICATIONS (hanya relevan di API 33+; layar yang memfilter SDK). */
    data object RequestNotificationPermission : NotificationSettingsEffect
}
