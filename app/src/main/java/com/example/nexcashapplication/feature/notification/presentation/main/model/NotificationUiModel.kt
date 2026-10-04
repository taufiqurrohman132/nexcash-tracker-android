package com.example.nexcashapplication.feature.notification.presentation.main.model

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
 * @param id stabil selama alert terus melewati threshold (`"$budgetId-$threshold"`),
 *   dipakai untuk menandai baca — bukan index, karena daftar bisa berubah tiap emisi Room.
 * @param budgetId id budget (String, sama dengan `Budget.id`) untuk navigasi ke layar budget.
 */
data class NotificationUiModel(
    val id: String,
    val budgetId: String,
    val title: String,
    val message: String,
    val timeLabel: String,
    val severity: AlertSeverity,
    val isRead: Boolean,
)

/** Sekumpulan notifikasi yang dikelompokkan berdasarkan hari. */
data class NotificationGroupUi(
    val header: String,
    val items: List<NotificationUiModel>,
)
