package com.example.nexcashapplication.feature.notification.domain.repository

/**
 * Riwayat notifikasi yang sudah dikirim ke status bar, tersimpan di Room (tabel `notifications`).
 *
 * Dua peran sekaligus:
 * 1. **Anti-spam** — alert hanya boleh dikirim sekali per periode anggaran.
 * 2. **Riwayat** — baris yang tersimpan menjelaskan apa, kapan, dan untuk budget apa
 *    notifikasi itu pernah dikirim.
 *
 * Kunci adalah `"$budgetId-$threshold@periodStart"`; tanpa bagian periode, "Oktober" tahun
 * lalu dan tahun ini dianggap sama dan alert tidak akan pernah dikirim ulang.
 */
interface NotificationHistoryRepository {

    /** true kalau notifikasi dengan [id] ini sudah pernah dikirim (dan tidak di-dismiss-repost). */
    suspend fun hasBeenSent(id: String): Boolean

    /** Catat pengiriman. Id sama → baris lama dipertahankan (INSERT OR IGNORE). */
    suspend fun record(
        id: String,
        type: String,
        title: String,
        body: String,
        /** Tujuan saat notifikasi di-tap, disimpan untuk riwayat (mis. "notifications"). */
        deepLink: String? = null,
        /** Id objek terkait (budgetId) kalau ada. */
        refId: String? = null,
    )
}
