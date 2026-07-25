package com.example.nexcashapplication.core.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "monthly_category_snapshots",
    foreignKeys = [
        ForeignKey(
            entity = MonthlySnapshotEntity::class,
            parentColumns = ["id"],
            childColumns = ["snapshot_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index(value = ["snapshot_id"]), Index(value = ["category_id"])],

    )
data class MonthlyCategorySnapshotEntity(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "snapshot_id")
    val snapshotId: Long,

    @ColumnInfo(name = "category_id")
    val categoryId: Long,

    @ColumnInfo(name = "total_amount")
    val totalAmount: Double = 0.0,

    @ColumnInfo(name = "budget_amount")
    val budgetAmount: Double = 0.0,

    @ColumnInfo(name = "transaction_count")
    val transactionCount: Int = 0,
)