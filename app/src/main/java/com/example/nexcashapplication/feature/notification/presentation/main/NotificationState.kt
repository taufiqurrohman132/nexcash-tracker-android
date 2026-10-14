package com.example.nexcashapplication.feature.notification.presentation.main

import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.feature.notification.presentation.main.model.NotificationGroupUi

data class NotificationState(
    /** true sampai emisi data pertama diterima — layar menampilkan indikator loading. */
    val isLoading: Boolean = true,

    /** Notifikasi yang sudah dikelompokkan per hari, terbaru di atas. */
    val groups: List<NotificationGroupUi> = emptyList(),

    /** Jumlah alert yang id-nya belum ada di [readIds]; dihitung ulang tiap data baru masuk. */
    val unreadCount: Int = 0,

    /**
     * Id yang sudah ditandai dibaca. Sengaja in-memory: sumber datanya turunan budget,
     * bukan tabel notifications, jadi status baca cukup bertahan selama layar terbuka.
     */
    val readIds: Set<String> = emptySet(),

    /** Gagal memuat alert; null = tidak ada error. */
    val error: UiText? = null,
) {
    /** Selesai loading tapi tidak ada anggaran yang melewati threshold → tampilkan empty state. */
    val isEmpty: Boolean
        get() = !isLoading && groups.isEmpty() && error == null
}
