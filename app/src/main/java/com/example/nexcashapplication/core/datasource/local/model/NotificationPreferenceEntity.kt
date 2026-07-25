package com.example.nexcashapplication.core.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "notification_preferences",
)
data class NotificationPreferenceEntity(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    // contoh: reminder, budget_alert, promo, transaction
    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "push_enabled")
    val pushEnabled: Boolean = true,

    @ColumnInfo(name = "in_app_enabled")
    val inAppEnabled: Boolean = true,
)
