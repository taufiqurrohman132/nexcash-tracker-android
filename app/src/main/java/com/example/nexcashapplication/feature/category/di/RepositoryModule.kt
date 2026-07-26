package com.example.nexcashapplication.feature.category.di

import com.example.nexcashapplication.core.datasource.local.dao.CategoryDao
import com.example.nexcashapplication.feature.category.data.CategoryRepositoryImpl
import com.example.nexcashapplication.feature.category.domain.repository.CategoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCategoryRepository(dao: CategoryDao): CategoryRepository =
        CategoryRepositoryImpl(dao)
}