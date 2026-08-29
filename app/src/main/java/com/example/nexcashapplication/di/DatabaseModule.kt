package com.example.nexcashapplication.di

import android.content.Context
import androidx.room.Room
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

    @Provides
    @Singleton
    fun provideNexCashDatabase(@ApplicationContext context: Context): NexCashDatabase =
        Room.databaseBuilder(
            context,
            NexCashDatabase::class.java,
            "nex_cash_db",
        ).build()

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