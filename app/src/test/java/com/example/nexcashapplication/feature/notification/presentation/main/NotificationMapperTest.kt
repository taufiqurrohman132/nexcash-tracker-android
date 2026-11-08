package com.example.nexcashapplication.feature.notification.presentation.main

import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.feature.notification.domain.model.BudgetAlert
import com.example.nexcashapplication.feature.notification.presentation.main.model.AlertSeverity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZoneId

class NotificationMapperTest {

    private val zone = ZoneId.of("Asia/Jakarta")

    @Test
    fun `toNotificationAlert - threshold di bawah 100 menghasilkan severity Warning`() {
        val alert = BudgetAlert(
            id = "b1-80",
            budgetId = "b1",
            categoryName = "Hiburan",
            percentUsed = 85,
            threshold = 80,
            periodLabel = "November",
            periodStart = 1000L,
        )

        val notificationAlert = alert.toNotificationAlert(seenAt = 5000L)

        assertEquals("b1-80", notificationAlert.id)
        assertEquals("b1", notificationAlert.budgetId)
        assertEquals(AlertSeverity.Warning, notificationAlert.severity)
        assertEquals(5000L, notificationAlert.seenAt)

        val titleRes = notificationAlert.title as UiText.Res
        assertEquals(R.string.notif_alert_title_format, titleRes.resId)
        assertEquals(listOf("Hiburan", 80), titleRes.args)

        val bodyRes = notificationAlert.message as UiText.Res
        assertEquals(R.string.notif_alert_warning_body_format, bodyRes.resId)
        assertEquals(listOf("November"), bodyRes.args)
    }

    @Test
    fun `toNotificationAlert - threshold 100 atau lebih menghasilkan severity Limit`() {
        val alert = BudgetAlert(
            id = "b1-100",
            budgetId = "b1",
            categoryName = "Hiburan",
            percentUsed = 100,
            threshold = 100,
            periodLabel = "November",
            periodStart = 1000L,
        )

        val notificationAlert = alert.toNotificationAlert(seenAt = 5000L)

        assertEquals(AlertSeverity.Limit, notificationAlert.severity)

        val bodyRes = notificationAlert.message as UiText.Res
        assertEquals(R.string.notif_alert_limit_body_format, bodyRes.resId)
        assertEquals(listOf("November"), bodyRes.args)
    }

    @Test
    fun `toGroupedUi - daftar kosong mengembalikan daftar grup kosong`() {
        val grouped = emptyList<NotificationAlert>().toGroupedUi(
            readIds = emptySet(),
            now = System.currentTimeMillis(),
            zone = zone,
        )
        assertTrue(grouped.isEmpty())
    }

    @Test
    fun `toGroupedUi - mengelompokkan alert hari ini dan kemarin dengan header yang tepat`() {
        val dateToday = LocalDate.of(2026, 11, 7)
        val now = dateToday.atTime(12, 0).atZone(zone).toInstant().toEpochMilli()

        // 30 menit yang lalu (hari ini)
        val seenToday = now - 30 * 60_000L
        // Kemarin jam 10:00
        val seenYesterday = dateToday.minusDays(1).atTime(10, 0).atZone(zone).toInstant().toEpochMilli()

        val alertToday = BudgetAlert("b1-80", "b1", "Makan", 85, 80, "Nov", 0L)
            .toNotificationAlert(seenAt = seenToday)
        val alertYesterday = BudgetAlert("b2-100", "b2", "Tagihan", 100, 100, "Nov", 0L)
            .toNotificationAlert(seenAt = seenYesterday)

        val grouped = listOf(alertYesterday, alertToday).toGroupedUi(
            readIds = setOf("b2-100"),
            now = now,
            zone = zone,
        )

        assertEquals(2, grouped.size)

        // Grup pertama: Hari ini
        val todayGroup = grouped[0]
        assertEquals(UiText.Res(R.string.notif_group_today), todayGroup.header)
        assertEquals(1, todayGroup.items.size)
        val itemToday = todayGroup.items[0]
        assertEquals("b1-80", itemToday.id)
        assertFalse(itemToday.isRead)
        // 30 menit yang lalu
        assertEquals(UiText.Res(R.string.notif_time_minutes_ago_format, listOf(30L)), itemToday.timeLabel)

        // Grup kedua: Kemarin
        val yesterdayGroup = grouped[1]
        assertEquals(UiText.Res(R.string.notif_group_yesterday), yesterdayGroup.header)
        assertEquals(1, yesterdayGroup.items.size)
        val itemYesterday = yesterdayGroup.items[0]
        assertEquals("b2-100", itemYesterday.id)
        assertTrue(itemYesterday.isRead)
        // Kemarin format waktu "10.00"
        val yesterdayTimeRes = itemYesterday.timeLabel as UiText.Res
        assertEquals(R.string.notif_time_yesterday_format, yesterdayTimeRes.resId)
    }

    @Test
    fun `toGroupedUi - timeLabel baru saja jika selisih waktu kurang dari 1 menit`() {
        val now = System.currentTimeMillis()
        val seenJustNow = now - 20_000L // 20 detik lalu

        val alert = BudgetAlert("b1-80", "b1", "Makan", 80, 80, "Nov", 0L)
            .toNotificationAlert(seenAt = seenJustNow)

        val grouped = listOf(alert).toGroupedUi(
            readIds = emptySet(),
            now = now,
            zone = zone,
        )

        assertEquals(UiText.Res(R.string.notif_time_just_now), grouped[0].items[0].timeLabel)
    }
}
