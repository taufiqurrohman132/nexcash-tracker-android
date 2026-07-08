package com.example.nexcashapplication.core.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bill_payments",
    foreignKeys = [
        ForeignKey(
            entity = BillEntity::class,
            parentColumns = ["id"],
            childColumns = ["bill_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transaction_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["bill_id"]),Index(value = ["transaction_id"]),Index(value = ["account_id"]),]
)
data class BillPaymentEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "bill_id")
    val billId: Long,

    @ColumnInfo(name = "transaction_id")
    val transactionId: Long? = null,

    @ColumnInfo(name = "account_id")
    val accountId: Long? = null,

    @ColumnInfo(name = "amount_paid")
    val amountPaid: Double,

    @ColumnInfo(name = "period_year")
    val periodYear: Short,

    @ColumnInfo(name = "period_month")
    val periodMonth: Short,

    // pending, paid, overdue, cancelled
    @ColumnInfo(name = "status")
    val status: String
)