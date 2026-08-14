package com.example.nexcashapplication.di

import com.example.nexcashapplication.core.datasource.repository.PreferencesRepositoryImpl
import com.example.nexcashapplication.core.domain.repository.PreferencesRepository
import com.example.nexcashapplication.feature.aset.data.AccountRepositoryImpl
import com.example.nexcashapplication.feature.aset.domain.repository.AccountRepository
import com.example.nexcashapplication.feature.category.data.CategoryRepositoryImpl
import com.example.nexcashapplication.feature.category.domain.repository.CategoryRepository
import com.example.nexcashapplication.feature.transaction.data.TransactionRepositoryImpl
import com.example.nexcashapplication.feature.transaction.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl,
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindTrasactionsRepository(
        impl: TransactionRepositoryImpl,
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindAccountRepository(
        impl: AccountRepositoryImpl,
//        fake: FakeAccountRepositoryImpl,
    ): AccountRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        impl: PreferencesRepositoryImpl,
    ): PreferencesRepository

}