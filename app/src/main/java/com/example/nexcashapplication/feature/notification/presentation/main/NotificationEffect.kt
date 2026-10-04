package com.example.nexcashapplication.feature.notification.presentation.main

/** Efek sekali jalan dari [NotificationViewModel] — dikumpulkan layar, bukan disimpan di state. */
sealed interface NotificationEffect {
    data object NavigateBack : NotificationEffect

    data object NavigateToSettings : NotificationEffect

    /** Buka layar budget terkait notifikasi yang diketap. */
    data class NavigateToBudget(val budgetId: String) : NotificationEffect
}
