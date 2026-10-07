package com.example.nexcashapplication.feature.notification.presentation.main

import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.core.i18n.uiText
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
    val title: UiText,
    val message: UiText,
    val severity: AlertSeverity,
    val seenAt: Long,
)

/** Domain → presentation: teks (belum di-resolve), severity, dan waktu pertama kali terlihat. */
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

/** Judul & badan sebuah alert — masih berupa [UiText], bukan [String] jadi. */
data class AlertText(val title: UiText, val body: UiText)

/**
 * Teks alert — **satu sumber** untuk daftar in-app maupun notifikasi status bar, supaya isi
 * keduanya tidak pernah berbeda. Publik karena dipakai `SystemAlertNotifier`.
 *
 * Berupa [UiText] (kunci resource + argumen), bukan teks jadi: mapper berjalan di luar
 * composable, jadi teksnya baru di-resolve — dengan bahasa aplikasi — di layar dan di
 * pengirim notifikasi.
 */
fun BudgetAlert.toNotificationText(): AlertText = AlertText(
    title = uiText(R.string.notif_alert_title_format, categoryName, threshold),
    body = if (threshold >= 100) {
        uiText(R.string.notif_alert_limit_body_format, periodLabel)
    } else {
        uiText(R.string.notif_alert_warning_body_format, periodLabel)
    },
)

private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH.mm", Locale.getDefault())
private val dayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())

/**
 * Kelompokkan alert per hari dan petakan ke model UI.
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

private fun headerFor(day: LocalDate, today: LocalDate, yesterday: LocalDate): UiText = when (day) {
    today -> uiText(R.string.notif_group_today)
    yesterday -> uiText(R.string.notif_group_yesterday)
    // Tanggal sudah jadi data (terformat Locale aktif), bukan terjemahan.
    else -> UiText.Raw(day.format(dayFormatter))
}

/** Label waktu relatif — semua teksnya lewat resource. Tanggal/jam tetap data. */
private fun timeLabelFor(seenAt: Long, now: Long, zone: ZoneId): UiText {
    val elapsed = now - seenAt
    if (elapsed < 60_000L) return uiText(R.string.notif_time_just_now)

    val seenZone = Instant.ofEpochMilli(seenAt).atZone(zone)
    val today = Instant.ofEpochMilli(now).atZone(zone).toLocalDate()
    return when (seenZone.toLocalDate()) {
        today -> when {
            elapsed < 60 * 60_000L -> uiText(R.string.notif_time_minutes_ago_format, elapsed / 60_000L)
            else -> uiText(R.string.notif_time_hours_ago_format, elapsed / (60 * 60_000L))
        }

        today.minusDays(1) -> uiText(
            R.string.notif_time_yesterday_format,
            seenZone.format(timeFormatter),
        )

        else -> uiText(
            R.string.notif_time_date_format,
            seenZone.format(dayFormatter),
            seenZone.format(timeFormatter),
        )
    }
}
