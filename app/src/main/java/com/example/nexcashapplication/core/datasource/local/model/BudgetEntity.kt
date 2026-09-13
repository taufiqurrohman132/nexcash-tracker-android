package com.example.nexcashapplication.core.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "budgets",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = BudgetEntity::class,
            parentColumns = ["id"], childColumns = ["recurring_group_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index(value = ["category_id"]),
        Index("recurring_group_id"),
        Index(value = ["recurring_group_id", "start_date"], unique = true)
    ],

    )
data class BudgetEntity(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "category_id")
    val categoryId: String,

    // definisi periode dasar
    @ColumnInfo(name = "start_date")
    val startDate: Long,       // epoch millis, tanggal mulai berlaku

    @ColumnInfo(name = "end_date")
    val endDate: Long,

    @ColumnInfo(name = "amount")
    val amount: Long,

    @ColumnInfo(name = "recurring_group_id")
    val recurringGroupId: Long?,

    @ColumnInfo(name = "is_recurring")
    val isRecurring: Boolean = true,

    @ColumnInfo(name = "period_type")
    val periodType: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
)