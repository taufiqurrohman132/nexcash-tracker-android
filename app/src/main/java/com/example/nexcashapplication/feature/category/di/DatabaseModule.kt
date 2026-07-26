package com.example.nexcashapplication.feature.category.di

import android.content.Context
import androidx.room.Room
import com.example.nexcashapplication.core.datasource.local.dao.CategoryDao
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


}