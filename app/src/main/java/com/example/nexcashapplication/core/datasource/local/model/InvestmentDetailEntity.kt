package com.example.nexcashapplication.core.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "investment_details",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["account_id"]),]

)
data class InvestmentDetailEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "account_id")
    val accountId: Long,

    // saham, reksadana, crypto, emas, obligasi, dll
    @ColumnInfo(name = "investment_type")
    val investmentType: String,

    @ColumnInfo(name = "return_rate")
    val returnRate: Double = 0.0,

    @ColumnInfo(name = "units")
    val units: Double = 0.0,

    @ColumnInfo(name = "unit_price")
    val unitPrice: Double = 0.0,

    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long? = null
)