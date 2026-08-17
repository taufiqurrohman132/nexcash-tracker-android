package com.example.nexcashapplication.core.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.UUID

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL,
        )
    ],
    indices = [Index(value = ["account_id"]), Index(value = ["category_id"])],
)
data class TransactionEntity(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "account_id")
    val accountId: String? = null,

    @ColumnInfo(name = "category_id")
    val categoryId: String? = null,

    // expense / income
    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "amount")
    val amount: Long,

    @ColumnInfo(name = "description")
    val description: String? = null,

    // manual / import / recurring / system
    @ColumnInfo(name = "source")
    val source: String,

    @ColumnInfo(name = "receipt_url")
    val receiptUrl: String? = null,

    @ColumnInfo(name = "transaction_date")
    val transactionDate: Long,

    @ColumnInfo(name = "deleted_at")
    val deletedAt: Long? = null,
)

data class TransactionWithDetailsEntity(
    @Embedded
    val transaction: TransactionEntity,

    @Relation(parentColumn = "account_id", entityColumn = "id")
    val account: AccountEntity? = null,

    @Relation(parentColumn = "category_id", entityColumn = "id")
    val category: CategoryEntity? = null,
)