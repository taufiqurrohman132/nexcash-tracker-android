package com.example.nexcashapplication.feature.category.domain.usecase

import com.example.nexcashapplication.feature.category.domain.model.Category
import com.example.nexcashapplication.feature.category.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoryByIdUseCase @Inject constructor(
    private val repository: CategoryRepository,
) {
    operator fun invoke(id: String): Flow<Category?> {
        return repository.getCategoryById(id)
    }
}
