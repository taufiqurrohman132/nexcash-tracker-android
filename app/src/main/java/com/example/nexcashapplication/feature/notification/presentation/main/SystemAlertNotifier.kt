package com.example.nexcashapplication.feature.notification.presentation.main

import android.content.Context
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.core.i18n.withAppLocale
import com.example.nexcashapplication.core.notification.NotificationHelper
import com.example.nexcashapplication.feature.notification.domain.model.BudgetAlert
import com.example.nexcashapplication.feature.notification.domain.repository.NotificationHistoryRepository
import com.example.nexcashapplication.feature.notification.domain.usecase.GetBudgetAlertsUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Mengirim alert budget ke status bar **selama proses aplikasi hidup**.
 *
 * Sengaja tanpa WorkManager: transaksi hanya bisa dibuat saat aplikasi terbuka, dan
 * [GetBudgetAlertsUseCase] adalah Flow Room yang langsung ber-emit begitu ada transaksi baru —
 * jadi observer ini sudah menangkap momen alert muncul tanpa perlu penjadwal apa pun.
 *
 * Saat aplikasi dibuka lagi flow ber-emit ulang dengan alert yang sama; yang menahannya adalah
 * riwayat di Room ([NotificationHistoryRepository]), bukan memori — kalau pakai memori, notifikasi
 * lama akan terkirim ulang tiap aplikasi dibuka.
 *
 * Master switch dan preferensi per budget tidak perlu dicek di sini: sudah otomatis, karena
 * `GetBudgetAlertsUseCase` memfilternya di dalam.
 */
class SystemAlertNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getBudgetAlertsUseCase: GetBudgetAlertsUseCase,
    private val notificationHistoryRepository: NotificationHistoryRepository,
    private val notificationHelper: NotificationHelper,
) {

    /** Mulai mengamati alert pada [scope] — panggil sekali dari `MyApps.onCreate`. */
    fun start(scope: CoroutineScope) {
        scope.launch {
            getBudgetAlertsUseCase()
                    .distinctUntilChanged()
                    .collect { alerts -> alerts.forEach { dispatch(it) } }
        }
    }

    private suspend fun dispatch(alert: BudgetAlert) {
        val key = dedupKey(alert)
        if (notificationHistoryRepository.hasBeenSent(key)) return

        // Izin belum diberikan → jangan dicatat, supaya alert terkirim begitu izin masuk.
        if (!notificationHelper.canPost()) return

        val text = alert.toNotificationText()
        val title = text.title.asString(context)
        val body = text.body.asString(context)
        notificationHelper.post(
            id = key.hashCode(),
            channelId = NotificationHelper.CHANNEL_BUDGET_ALERT,
            title = title,
            body = body,
            destination = NotificationHelper.DEST_NOTIFICATIONS,
        )
        notificationHistoryRepository.record(
            id = key,
            type = "budget_alert",
            title = title,
            body = body,
            deepLink = NotificationHelper.DEST_NOTIFICATIONS,
            refId = alert.budgetId,
        )
    }

    companion object {
        /**
         * Kunci anti-spam: id alert + awal periode. Tanpa bagian `periodStart`, "Oktober" tahun
         * lalu dan tahun ini dianggap sama sehingga alert tidak akan pernah dikirim ulang.
         *
         * Dipakai juga sebagai id notifikasi (di-hash) supaya alert yang sama menimpa dirinya
         * sendiri, bukan menumpuk.
         */
        fun dedupKey(alert: BudgetAlert): String = "${alert.id}@${alert.periodStart}"
    }
}

/**
 * Resolve [UiText] di luar composable dengan **bahasa aplikasi** (bukan bahasa perangkat):
 * context class NotificationHelper adalah Application context yang tidak ikut AppLocale.
 */
private fun UiText.asString(context: Context): String = when (this) {
    is UiText.Res -> context.withAppLocale().getString(resId, *args.toTypedArray())
    is UiText.Raw -> value
}
