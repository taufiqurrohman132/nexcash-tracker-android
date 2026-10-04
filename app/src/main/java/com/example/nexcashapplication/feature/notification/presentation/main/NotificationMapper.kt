package com.example.nexcashapplication.feature.notification.presentation.main

import com.example.nexcashapplication.feature.notification.domain.model.BudgetAlert
import com.example.nexcashapplication.feature.notification.presentation.main.model.AlertSeverity
import com.example.nexcashapplication.feature.notification.presentation.main.model.NotificationGroupUi
import com.example.nexcashapplication.feature.notification.presentation.main.model.NotificationUiModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Peringatan anggaran mentah sebelum masuk state: satu alert = satu threshold yang terlewati.
 *
 * @param seenAt epoch millis saat alert **pertama kali** terlihat oleh ViewModel. Disimpan di memori
 *   ViewModel supaya label waktu & grup tidak loncat tiap emisi Room memancar ulang data yang sama.
 */
internal data class NotificationAlert(
    val id: String,
    val budgetId: String,
    val title: String,
    val message: String,
    val severity: AlertSeverity,
    val seenAt: Long,
)

/** Domain → presentation: teks Indonesia, severity, dan waktu pertama kali terlihat. */
internal fun BudgetAlert.toNotificationAlert(seenAt: Long): NotificationAlert {
    val text = toNotificationText()
    return NotificationAlert(
        id = id,
        budgetId = budgetId,
        title = text.title,
        message = text.body,
        severity = if (threshold >= 100) AlertSeverity.Limit else AlertSeverity.Warning,
        seenAt = seenAt,
    )
}

/** Judul & badan sebuah alert. */
data class AlertText(val title: String, val body: String)

/**
 * Teks alert — **satu sumber** untuk daftar in-app maupun notifikasi status bar, supaya isi
 * keduanya tidak pernah berbeda. Publik karena dipakai `SystemAlertNotifier`.
 */
fun BudgetAlert.toNotificationText(): AlertText = AlertText(
    title = "$categoryName mencapai $threshold%",
    body = if (threshold >= 100) {
        "Batas anggaran $periodLabel sudah tercapai."
    } else {
        "Anggaran $periodLabel hampir habis."
    },
)

private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH.mm", Locale.getDefault())
private val dayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())

/**
 * Kelompokkan alert per hari ("Hari ini" / "Kemarin" / "3 Okt") dan petakan ke model UI.
 *
 * Terbaru di atas, lalu di dalam grup terbaru di atas. Mapping dilakukan di background
 * (lihat `flowOn(Dispatchers.Default)` di ViewModel), bukan di dalam `updateState`.
 */
internal fun List<NotificationAlert>.toGroupedUi(
    readIds: Set<String>,
    now: Long = System.currentTimeMillis(),
    zone: ZoneId = ZoneId.systemDefault(),
): List<NotificationGroupUi> {
    if (isEmpty()) return emptyList()

    val today = Instant.ofEpochMilli(now).atZone(zone).toLocalDate()
    val yesterday = today.minusDays(1)

    return sortedByDescending { it.seenAt }
            .groupBy { Instant.ofEpochMilli(it.seenAt).atZone(zone).toLocalDate() }
            .map { (day, alerts) ->
                NotificationGroupUi(
                    header = headerFor(day, today, yesterday),
                    items = alerts.map { alert ->
                        NotificationUiModel(
                            id = alert.id,
                            budgetId = alert.budgetId,
                            title = alert.title,
                            message = alert.message,
                            timeLabel = timeLabelFor(alert.seenAt, now, zone),
                            severity = alert.severity,
                            isRead = alert.id in readIds,
                        )
                    },
                )
            }
}

private fun headerFor(day: LocalDate, today: LocalDate, yesterday: LocalDate): String = when (day) {
    today -> "Hari ini"
    yesterday -> "Kemarin"
    else -> day.format(dayFormatter)
}

/** "Baru saja" / "5 menit lalu" / "3 jam lalu" / "Kemarin, 18.40" / "3 Okt, 18.40". */
private fun timeLabelFor(seenAt: Long, now: Long, zone: ZoneId): String {
    val elapsed = now - seenAt
    if (elapsed < 60_000L) return "Baru saja"

    val seenZone = Instant.ofEpochMilli(seenAt).atZone(zone)
    val today = Instant.ofEpochMilli(now).atZone(zone).toLocalDate()
    return when (seenZone.toLocalDate()) {
        today -> when {
            elapsed < 60 * 60_000L -> "${elapsed / 60_000L} menit lalu"
            else -> "${elapsed / (60 * 60_000L)} jam lalu"
        }

        today.minusDays(1) -> "Kemarin, ${seenZone.format(timeFormatter)}"
        else -> "${seenZone.format(dayFormatter)}, ${seenZone.format(timeFormatter)}"
    }
}
