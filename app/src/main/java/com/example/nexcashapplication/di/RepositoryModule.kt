package com.example.nexcashapplication.di

import com.example.nexcashapplication.core.datasource.repository.PreferencesRepositoryImpl
import com.example.nexcashapplication.core.domain.repository.PreferencesRepository
import com.example.nexcashapplication.feature.category.data.CategoryRepositoryImpl
import com.example.nexcashapplication.feature.category.domain.repository.CategoryRepository
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
    abstract fun bindPreferencesRepository(
        impl: PreferencesRepositoryImpl,
    ): PreferencesRepository

}