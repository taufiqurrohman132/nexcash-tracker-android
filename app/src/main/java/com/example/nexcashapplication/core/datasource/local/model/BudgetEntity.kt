package com.example.nexcashapplication.core.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "budgets",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["category_id"]),]

)
data class BudgetEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,


    @ColumnInfo(name = "category_id")
    val categoryId: Long,

    @ColumnInfo(name = "period_year")
    val periodYear: Short,

    // 1 - 12
    @ColumnInfo(name = "period_month")
    val periodMonth: Short,

    @ColumnInfo(name = "amount")
    val amount: Double
)