package com.example.nexcashapplication.core.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "insights",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["related_category_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class InsightEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "related_category_id")
    val relatedCategoryId: Long? = null,

    // contoh: spending, saving, budget, trend, reminder
    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "body")
    val body: String,

    @ColumnInfo(name = "action_label")
    val actionLabel: String? = null,

    @ColumnInfo(name = "action_type")
    val actionType: String? = null,

    @ColumnInfo(name = "is_read")
    val isRead: Boolean = false,

    @ColumnInfo(name = "period_year")
    val periodYear: Short,

    @ColumnInfo(name = "period_month")
    val periodMonth: Short,

    @ColumnInfo(name = "expires_at")
    val expiresAt: Long? = null
)