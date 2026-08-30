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
        )
    ],
    indices = [Index(value = ["category_id"])],

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
    val endDate: Long?,        // null = berlaku terus (recurring tanpa batas)

    @ColumnInfo(name = "amount")
    val amount: Long,

    @ColumnInfo(name = "recurrence_type")
    val recurrenceType: String,

    @ColumnInfo(name = "is_recurring")
    val isRecurring: Boolean = true,
)