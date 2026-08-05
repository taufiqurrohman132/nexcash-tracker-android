package com.example.nexcashapplication.di

import com.example.nexcashapplication.feature.category.domain.repository.CategoryRepository
import com.example.nexcashapplication.feature.category.domain.usecase.CategoryUseCases
import com.example.nexcashapplication.feature.category.domain.usecase.DeleteCategoryUseCase
import com.example.nexcashapplication.feature.category.domain.usecase.GetCategoryByIdUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideCategoryUseCases(repository: CategoryRepository): CategoryUseCases {
        return CategoryUseCases(
            getCategoryById = GetCategoryByIdUseCase(repository),
            deleteCategory = DeleteCategoryUseCase(repository)
        )
    }
}
