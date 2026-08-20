package com.example.nexcashapplication.feature.category.domain.usecase

import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.feature.category.domain.model.CategoryDeleteResolution
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository,
) {
    suspend operator fun invoke(categoryId: String, resolution: CategoryDeleteResolution) {
        repository.deleteCategory(categoryId, resolution)
    }
}
