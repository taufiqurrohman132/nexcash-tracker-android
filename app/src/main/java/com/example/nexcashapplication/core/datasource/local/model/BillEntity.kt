package com.example.nexcashapplication.core.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bills",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["category_id"]),]

)
data class BillEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "category_id")
    val categoryId: Long? = null,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "provider")
    val provider: String? = null,

    @ColumnInfo(name = "amount")
    val amount: Double,

    // monthly, weekly, yearly, custom
    @ColumnInfo(name = "frequency")
    val frequency: String,

    // format: yyyy-MM-dd
    @ColumnInfo(name = "next_due_date")
    val nextDueDate: String,

    // contoh: [1,3,7] = H-1, H-3, H-7
    @ColumnInfo(name = "reminder_days")
    val reminderDays: List<Int> = emptyList(),

    @ColumnInfo(name = "notify_enabled")
    val notifyEnabled: Boolean = true,

    @ColumnInfo(name = "deleted_at")
    val deletedAt: Long? = null
)
