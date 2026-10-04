package com.example.nexcashapplication.core.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.nexcashapplication.MainActivity
import com.example.nexcashapplication.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Pembungkus sistem notifikasi: channel, cek izin, dan kirim notifikasi lokal.
 *
 * Sengaja **tidak** tahu apa pun tentang `BudgetAlert` — pemanggil mengirim teks jadi, sehingga
 * helper ini netral dipakai untuk alert budget maupun reminder harian. Helper juga yang menentukan
 * tujuan navigasi saat notifikasi di-tap (lihat [EXTRA_DESTINATION]).
 *
 * Panggil [ensureChannels] sekali saat aplikasi start (`MyApps.onCreate`) — tanpa channel,
 * `notify()` gagal diam-diam di API 26+.
 */
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun ensureChannels() {
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_BUDGET_ALERT,
                "Peringatan anggaran",
                NotificationManager.IMPORTANCE_HIGH,
            ),
        )
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_REMINDER,
                "Pengingat harian",
                NotificationManager.IMPORTANCE_DEFAULT,
            ),
        )
    }

    /**
     * true kalau notifikasi benar-benar akan sampai. Di API 33+ izin POST_NOTIFICATIONS harus
     * diberikan, kalau tidak `notify()` gagal tanpa pesan — jadi dicek sebelum mencatat riwayat,
     * supaya alert tidak hilang percuma (akan dikirim lagi setelah izin diberikan).
     */
    fun canPost(): Boolean {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        }
        return true
    }

    /**
     * Kirim notifikasi. [id] menentukan notifikasi mana yang ditimpa — pakai kunci anti-spam
     * yang sudah di-hash agar alert yang sama menimpa dirinya sendiri, bukan menumpuk.
     */
    fun post(
        id: Int,
        channelId: String,
        title: String,
        body: String,
        destination: String,
    ) {
        if (!canPost()) return

        val contentIntent = PendingIntent.getActivity(
            context,
            id,
            Intent(context, MainActivity::class.java).apply {
                putExtra(EXTRA_DESTINATION, destination)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_stat_alert)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat.from(context).notify(id, notification)
    }

    companion object {
        const val CHANNEL_BUDGET_ALERT = "budget_alert"
        const val CHANNEL_REMINDER = "daily_reminder"

        /** Extra intent yang menentukan layar tujuan setelah notifikasi di-tap. */
        const val EXTRA_DESTINATION =
            "com.example.nexcashapplication.EXTRA_NOTIFICATION_DESTINATION"

        /** Nilai [EXTRA_DESTINATION] untuk notifikasi apa pun — layar daftar notifikasi. */
        const val DEST_NOTIFICATIONS = "notifications"
    }
}
