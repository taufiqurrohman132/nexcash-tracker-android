package com.example.nexcashapplication.core.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "accounts",
)
data class AccountEntity(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    // contoh: cash, bank, ewallet, credit_card, investment
    @ColumnInfo(name = "account_type")
    val accountType: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "institution")
    val institution: String? = null,

    @ColumnInfo(name = "account_number")
    val accountNumber: String? = null,

    @ColumnInfo(name = "current_balance")
    val currentBalance: Double = 0.0,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "deleted_at")
    val deletedAt: Long? = null,
)
