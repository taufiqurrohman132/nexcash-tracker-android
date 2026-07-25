package com.example.nexcashapplication.core.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "notifications",
)
data class NotificationEntity(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    // contoh: reminder, budget_alert, transaction, promo
    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "body")
    val body: String,

    @ColumnInfo(name = "deep_link")
    val deepLink: String? = null,

    @ColumnInfo(name = "ref_id")
    val refId: String? = null,

    @ColumnInfo(name = "is_read")
    val isRead: Boolean = false,

    @ColumnInfo(name = "sent_at")
    val sentAt: Long,
)
