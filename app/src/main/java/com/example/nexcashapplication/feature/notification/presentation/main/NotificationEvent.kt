package com.example.nexcashapplication.feature.notification.presentation.main

sealed interface NotificationEvent {
    /** Tombol panah di top bar. */
    data object BackClicked : NotificationEvent

    /** Tombol gear di top bar → layar pengaturan notifikasi. */
    data object SettingsClicked : NotificationEvent

    /**
     * Satu item diketap: tandai terbaca lalu buka budget-nya.
     * @param id id item (untuk status baca), [budgetId] tujuan navigasi.
     */
    data class ItemClicked(val id: String, val budgetId: String) : NotificationEvent

    /** "Tandai semua dibaca". */
    data object MarkAllReadClicked : NotificationEvent
}
