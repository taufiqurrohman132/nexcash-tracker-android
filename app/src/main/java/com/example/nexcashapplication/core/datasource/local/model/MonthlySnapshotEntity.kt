package com.example.nexcashapplication.core.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "monthly_snapshots",
)
data class MonthlySnapshotEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "period_year")
    val periodYear: Short,

    @ColumnInfo(name = "period_month")
    val periodMonth: Short,

    @ColumnInfo(name = "total_income")
    val totalIncome: Double,

    @ColumnInfo(name = "total_expense")
    val totalExpense: Double,

    @ColumnInfo(name = "savings_rate")
    val savingsRate: Double,

    @ColumnInfo(name = "net_worth")
    val netWorth: Double,
)
