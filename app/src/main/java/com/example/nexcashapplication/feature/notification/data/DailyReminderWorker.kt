package com.example.nexcashapplication.feature.notification.data

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.nexcashapplication.core.notification.NotificationHelper
import com.example.nexcashapplication.feature.notification.domain.repository.NotificationHistoryRepository
import com.example.nexcashapplication.feature.notification.domain.usecase.HasTransactionTodayUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate

/**
 * Pengingat harian: kalau seharian belum ada transaksi sama sekali, kirim notifikasi.
 *
 * Dijadwalkan WorkManager (lihat `MyApps.scheduleDailyReminder`) dengan initial delay menuju
 * pukul 21.00 — **bukan alarm eksak**: periode berikutnya dihitung WorkManager dari titik jalan
 * pertama, jadi jamnya bisa bergeser. Sifatnya "sekitar malam", bukan tepat 21.00.
 *
 * Tidak punya toggle sendiri; hanya izin sistem yang menentukan (lihat [NotificationHelper.canPost]).
 */
@HiltWorker
class DailyReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val hasTransactionToday: HasTransactionTodayUseCase,
    private val notificationHistoryRepository: NotificationHistoryRepository,
    private val notificationHelper: NotificationHelper,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = runCatching {
        // Sudah catat hari ini → tidak usah diingatkan.
        if (hasTransactionToday()) return@runCatching

        val today = LocalDate.now()
        val id = "reminder@${today.toEpochDay()}"

        // Gerbang anti-spam per hari — kalau jalur ini keburu jalan dua kali.
        if (notificationHistoryRepository.hasBeenSent(id)) return@runCatching
        if (!notificationHelper.canPost()) return@runCatching

        val title = "Belum ada transaksi hari ini"
        val body = "Catat pemasukan atau pengeluaranmu supaya anggaranmu tetap akurat."

        notificationHelper.post(
            id = id.hashCode(),
            channelId = NotificationHelper.CHANNEL_REMINDER,
            title = title,
            body = body,
            destination = NotificationHelper.DEST_NOTIFICATIONS,
        )
        notificationHistoryRepository.record(
            id = id,
            type = "reminder",
            title = title,
            body = body,
            deepLink = NotificationHelper.DEST_NOTIFICATIONS,
        )
    }.fold(
        onSuccess = { Result.success() },
        // Gagal sementara (mis. DB sibuk) → coba lagi dengan backoff bawaan WorkManager.
        onFailure = { Result.retry() },
    )

    companion object {
        /** Nama unique work supaya penjadwalan ulang tiap start aplikasi tidak menduplikasi. */
        const val UNIQUE_NAME = "daily_no_transaction_reminder"
    }
}
