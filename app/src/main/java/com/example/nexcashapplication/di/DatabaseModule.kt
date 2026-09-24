package com.example.nexcashapplication.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.nexcashapplication.core.datasource.local.dao.AccountDao
import com.example.nexcashapplication.core.datasource.local.dao.BudgetDao
import com.example.nexcashapplication.core.datasource.local.dao.CategoryDao
import com.example.nexcashapplication.core.datasource.local.dao.TransactionDao
import com.example.nexcashapplication.core.datasource.local.database.NexCashDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // v1 → v2: data-only migration. Semua root lama (recurring_group_id NULL) di-self-reference-kan
    // supaya unique index (recurring_group_id, start_date) benar-benar melindungi root dari duplikat.
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("UPDATE budgets SET recurring_group_id = id WHERE recurring_group_id IS NULL")
        }
    }

    @Provides
    @Singleton
    fun provideNexCashDatabase(@ApplicationContext context: Context): NexCashDatabase =
        Room.databaseBuilder(
            context,
            NexCashDatabase::class.java,
            "nex_cash_db",
        ).addMigrations(MIGRATION_1_2).build()

    @Provides
    @Singleton
    fun provideCategoryDao(database: NexCashDatabase): CategoryDao =
        database.categoryDao()

    @Provides
    @Singleton
    fun provideAccountDao(database: NexCashDatabase): AccountDao =
        database.accountDao()

    @Provides
    @Singleton
    fun provideTransactionDao(database: NexCashDatabase): TransactionDao =
        database.transactionDao()

    @Provides
    @Singleton
    fun provideBudgetDao(database: NexCashDatabase): BudgetDao =
        database.budgetDao()


}