package com.example.nexcashapplication.core.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Preferensi peringatan notifikasi per budget.
 *
 * Satu baris = satu budget; [id] sengaja diisi dengan budgetId (bukan UUID) supaya
 * `@Insert(onConflict = REPLACE)` meng-upsert per budget tanpa index unik tambahan.
 *
 * Kolom lama ([type], [pushEnabled], [inAppEnabled]) dipertahankan: migrasi hanya boleh
 * ADD COLUMN, membuang kolom berarti membangun ulang tabel.
 */
@Entity(
    tableName = "notification_preferences",
)
data class NotificationPreferenceEntity(

    @PrimaryKey
    val id: String,

    // contoh: reminder, budget_alert, promo, transaction
    @ColumnInfo(name = "type")
    val type: String = "budget_alert",

    @ColumnInfo(name = "push_enabled")
    val pushEnabled: Boolean = true,

    @ColumnInfo(name = "in_app_enabled")
    val inAppEnabled: Boolean = true,

    /** Budget terkait; sama dengan [id] — dipisah agar query berintent jelas. */
    @ColumnInfo(name = "budget_id")
    val budgetId: String,

    /** Threshold aktif sebagai CSV, mis. "80,100". */
    @ColumnInfo(name = "thresholds")
    val thresholds: String,

    /** Saklar peringatan untuk budget ini. */
    @ColumnInfo(name = "enabled")
    val enabled: Boolean,
)
