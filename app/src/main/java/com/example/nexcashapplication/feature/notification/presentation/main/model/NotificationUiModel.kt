package com.example.nexcashapplication.feature.notification.presentation.main.model

import com.example.nexcashapplication.core.i18n.UiText

/** Level keparahan peringatan anggaran yang tampil di layar notifikasi. */
enum class AlertSeverity {
    /** Sudah terpakai ≥80% tapi belum 100% — anggaran hampir habis. */
    Warning,

    /** Sudah terpakai ≥100% — batas anggaran tercapai. */
    Limit,
}

/**
 * Satu baris notifikasi siap tampil.
 *
 * Judul/badan/grup/waktu memakai [UiText] — di-resolve (`asString()`) saat ditampilkan,
 * bukan di mapper yang berjalan di luar composable.
 *
 * @param id stabil selama alert terus melewati threshold (`"$budgetId-$threshold"`),
 *   dipakai untuk menandai baca — bukan index, karena daftar bisa berubah tiap emisi Room.
 * @param budgetId id budget (String, sama dengan `Budget.id`) untuk navigasi ke layar budget.
 */
data class NotificationUiModel(
    val id: String,
    val budgetId: String,
    val title: UiText,
    val message: UiText,
    val timeLabel: UiText,
    val severity: AlertSeverity,
    val isRead: Boolean,
)

/** Sekumpulan notifikasi yang dikelompokkan berdasarkan hari. */
data class NotificationGroupUi(
    val header: UiText,
    val items: List<NotificationUiModel>,
)
