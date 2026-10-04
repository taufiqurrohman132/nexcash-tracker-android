package com.example.nexcashapplication.core.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nexcashapplication.core.datasource.local.model.NotificationEntity

/**
 * Riwayat notifikasi yang **sudah dikirim** ke status bar — bukan daftar yang tampil di layar
 * (layar notifikasi dihitung dari budget, lihat `GetBudgetAlertsUseCase`).
 *
 * Dipakai sebagai gerbang anti-spam: satu baris = satu pengiriman, primary key = kunci alert.
 */
@Dao
interface NotificationDao {

    @Query("SELECT COUNT(*) FROM notifications WHERE id = :id")
    suspend fun countById(id: String): Int

    /** Abaikan kalau sudah ada — pemanggil cukup menulis tanpa mengecek dulu. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(notification: NotificationEntity)
}
