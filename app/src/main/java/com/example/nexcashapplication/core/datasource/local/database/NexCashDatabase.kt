package com.example.nexcashapplication.core.datasource.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.nexcashapplication.core.datasource.local.converter.Converters
import com.example.nexcashapplication.core.datasource.local.dao.AccountDao
import com.example.nexcashapplication.core.datasource.local.dao.BillDao
import com.example.nexcashapplication.core.datasource.local.dao.BillPaymentDao
import com.example.nexcashapplication.core.datasource.local.dao.BudgetDao
import com.example.nexcashapplication.core.datasource.local.dao.CategoryDao
import com.example.nexcashapplication.core.datasource.local.dao.InsightDao
import com.example.nexcashapplication.core.datasource.local.dao.InvestmentDetailDao
import com.example.nexcashapplication.core.datasource.local.dao.MonthlyCategorySnapshotDao
import com.example.nexcashapplication.core.datasource.local.dao.MonthlySnapshotDao
import com.example.nexcashapplication.core.datasource.local.dao.NotificationDao
import com.example.nexcashapplication.core.datasource.local.dao.NotificationPreferenceDao
import com.example.nexcashapplication.core.datasource.local.dao.TransactionDao
import com.example.nexcashapplication.core.datasource.local.model.AccountEntity
import com.example.nexcashapplication.core.datasource.local.model.BillEntity
import com.example.nexcashapplication.core.datasource.local.model.BillPaymentEntity
import com.example.nexcashapplication.core.datasource.local.model.BudgetEntity
import com.example.nexcashapplication.core.datasource.local.model.CategoryEntity
import com.example.nexcashapplication.core.datasource.local.model.InsightEntity
import com.example.nexcashapplication.core.datasource.local.model.InvestmentDetailEntity
import com.example.nexcashapplication.core.datasource.local.model.MonthlyCategorySnapshotEntity
import com.example.nexcashapplication.core.datasource.local.model.MonthlySnapshotEntity
import com.example.nexcashapplication.core.datasource.local.model.NotificationEntity
import com.example.nexcashapplication.core.datasource.local.model.NotificationPreferenceEntity
import com.example.nexcashapplication.core.datasource.local.model.TransactionEntity

@Database(
    entities = [
        AccountEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        BillEntity::class,
        BillPaymentEntity::class,
        BudgetEntity::class,
        InsightEntity::class,
        InvestmentDetailEntity::class,
        MonthlyCategorySnapshotEntity::class,
        MonthlySnapshotEntity::class,
        NotificationEntity::class,
        NotificationPreferenceEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NexCashDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao

    abstract fun categoryDao(): CategoryDao

    abstract fun transactionDao(): TransactionDao

    abstract fun billDao(): BillDao

    abstract fun billPaymentDao(): BillPaymentDao

    abstract fun budgetDao(): BudgetDao

    abstract fun insightDao(): InsightDao

    abstract fun investmentDetailDao(): InvestmentDetailDao

    abstract fun monthlyCategorySnapshotDao(): MonthlyCategorySnapshotDao

    abstract fun monthlySnapshotDao(): MonthlySnapshotDao

    abstract fun notificationDao(): NotificationDao

    abstract fun notificationPreferenceDao(): NotificationPreferenceDao
}