package com.example.nexcashapplication.feature.category.domain.usecase

import javax.inject.Inject

data class CategoryUseCases @Inject constructor(
    val getCategoryById: GetCategoryByIdUseCase,
    val deleteCategory: DeleteCategoryUseCase,
    // Tambahkan use case lainnya di sini
)
