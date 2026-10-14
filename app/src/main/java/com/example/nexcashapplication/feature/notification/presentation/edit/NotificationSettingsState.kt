package com.example.nexcashapplication.feature.notification.presentation.edit

import com.example.nexcashapplication.core.i18n.UiText

data class NotificationSettingsState(
    /** true sampai daftar budget aktif pertama kali masuk. */
    val isLoading: Boolean = true,

    /** Saklar utama "Aktifkan pengingat" (master switch). */
    val masterEnabled: Boolean = true,

    /** Izin POST_NOTIFICATIONS sudah diberikan — mengontrol hint izin & permintaan izin. */
    val permissionGranted: Boolean = true,

    /** Nilai saklar utama yang tampil; sama dengan [masterEnabled] selama belum ada override. */
    val remindersActive: Boolean = true,

    /** Kartu per-budget, hanya anggaran expense yang sedang aktif. */
    val budgets: List<BudgetAlertUi> = emptyList(),

    /** Gagal memuat daftar budget; null = tidak ada error. */
    val error: UiText? = null,
)
