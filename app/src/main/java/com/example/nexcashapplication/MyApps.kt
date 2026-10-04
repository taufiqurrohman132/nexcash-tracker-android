package com.example.nexcashapplication

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.nexcashapplication.core.notification.NotificationHelper
import com.example.nexcashapplication.feature.budgets.domain.usecase.EnsureUpToDateBudgetsUseCase
import com.example.nexcashapplication.feature.notification.data.DailyReminderWorker
import com.example.nexcashapplication.feature.notification.presentation.main.SystemAlertNotifier
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MyApps : Application(), Configuration.Provider {

    /** Dibutuhkan WorkManager untuk membangun worker bertanda @HiltWorker. */
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var ensureUpToDateBudgets: EnsureUpToDateBudgetsUseCase

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var systemAlertNotifier: SystemAlertNotifier

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Menggantikan initializer bawaan WorkManager (dimatikan di AndroidManifest). Dibaca saat
     * pertama kali `WorkManager.getInstance()` dipanggil — yaitu setelah injeksi Hilt di bawah,
     * jadi [workerFactory] sudah terisi.
     */
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()

    override fun onCreate() {
        super.onCreate() // injeksi field @Inject selesai di sini

        // Wajib sebelum notify() apa pun; aman dipanggil berulang.
        notificationHelper.ensureChannels()

        // Lazy generation: generate periode anggaran berikutnya saat aplikasi dibuka.
        // Flow Room akan re-emit begitu baris baru masuk, jadi UI ikut tersinkron.
        applicationScope.launch {
            runCatching { ensureUpToDateBudgets() }
                .onFailure { Log.e(TAG, "Gagal generate periode anggaran", it) }
        }

        // Alert budget: reaktif terhadap transaksi, tanpa WorkManager — lihat kelasnya.
        systemAlertNotifier.start(applicationScope)

        scheduleDailyReminder()
    }

    /**
     * Pengingat "belum ada transaksi hari ini" sekitar pukul 21.00.
     *
     * `KEEP` membuat penjadwalan ini idempotent — dipanggil tiap start aplikasi, tetapi work
     * yang sudah ada tidak diganti (jadwalnya jangan di-reset tiap kali app dibuka).
     */
    private fun scheduleDailyReminder() {
        val request = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(millisUntilNext(DAILY_REMINDER_HOUR), TimeUnit.MILLISECONDS)
                .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            DailyReminderWorker.UNIQUE_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    /**
     * Jarak menuju [hour] berikutnya — initial delay untuk penjadwalan di atas.
     * Sudah lewat jamnya → hitung ke hari berikutnya.
     */
    private fun millisUntilNext(
        hour: Int,
        now: ZonedDateTime = ZonedDateTime.now(),
    ): Long {
        var next = now.toLocalDate().atTime(hour, 0).atZone(now.zone)
        if (!next.isAfter(now)) next = next.plusDays(1)
        return Duration.between(now, next).toMillis()
    }

    private companion object {
        const val TAG = "MyApps"

        /** Jam pengingat — perkiraan, bukan jam eksak (WorkManager bukan alarm). */
        const val DAILY_REMINDER_HOUR = 21
    }
}
