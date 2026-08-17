package com.example.nexcashapplication.feature.transaction.domain.usecase

import com.example.nexcashapplication.feature.category.domain.repository.CategoryRepository
import javax.inject.Inject

class GetAllCategoryUsecase @Inject constructor(
    private val repository: CategoryRepository,
) {
    operator fun invoke() = repository.getAllCategory()
}
