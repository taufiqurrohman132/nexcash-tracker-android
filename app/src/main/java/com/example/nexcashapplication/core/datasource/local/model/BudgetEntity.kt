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
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index(value = ["category_id"])],

    )
data class BudgetEntity(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),


    @ColumnInfo(name = "category_id")
    val categoryId: String,

    @ColumnInfo(name = "period_year")
    val periodYear: Short,

    // 1 - 12
    @ColumnInfo(name = "period_month")
    val periodMonth: Short,

    @ColumnInfo(name = "amount")
    val amount: Double,
)