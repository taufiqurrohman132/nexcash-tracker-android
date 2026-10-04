package com.example.nexcashapplication.di

import com.example.nexcashapplication.core.datasource.repository.BudgetRepositoryImpl
import com.example.nexcashapplication.core.datasource.repository.CategoryRepositoryImpl
import com.example.nexcashapplication.core.datasource.repository.PreferencesRepositoryImpl
import com.example.nexcashapplication.core.datasource.repository.TransactionRepositoryImpl
import com.example.nexcashapplication.core.domain.repository.BudgetRepository
import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.core.domain.repository.PreferencesRepository
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.account.data.AccountRepositoryImpl
import com.example.nexcashapplication.feature.account.domain.repository.AccountRepository
import com.example.nexcashapplication.feature.notification.data.NotificationHistoryRepositoryImpl
import com.example.nexcashapplication.feature.notification.data.NotificationPreferenceRepositoryImpl
import com.example.nexcashapplication.feature.notification.domain.repository.NotificationHistoryRepository
import com.example.nexcashapplication.feature.notification.domain.repository.NotificationPreferenceRepository
import com.example.nexcashapplication.feature.settings.data.SettingsRepositoryImpl
import com.example.nexcashapplication.feature.settings.domain.repository.SettingsRepository
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
    abstract fun bindBudgetRepository(
        impl: BudgetRepositoryImpl,
    ): BudgetRepository

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

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl,
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindNotificationPreferenceRepository(
        impl: NotificationPreferenceRepositoryImpl,
    ): NotificationPreferenceRepository

    @Binds
    @Singleton
    abstract fun bindNotificationHistoryRepository(
        impl: NotificationHistoryRepositoryImpl,
    ): NotificationHistoryRepository

}