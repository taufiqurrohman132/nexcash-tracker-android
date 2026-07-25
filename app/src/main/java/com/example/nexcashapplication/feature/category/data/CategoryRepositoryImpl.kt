package com.example.nexcashapplication.feature.category.data

import com.example.nexcashapplication.core.datasource.local.dao.CategoryDao
import com.example.nexcashapplication.core.datasource.local.mapper.toDomain
import com.example.nexcashapplication.core.datasource.local.mapper.toEntity
import com.example.nexcashapplication.feature.category.domain.model.Category
import com.example.nexcashapplication.feature.category.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    val dao: CategoryDao,
) : CategoryRepository {
    override suspend fun getCategoryById(id: String): Category? {
        return dao.getCategoryById(id)?.toDomain()
    }

    override suspend fun getAllCategory(): Flow<List<Category?>> {
    }

    override fun searchCategories(
        query: String,
        type: String,
    ): Flow<List<Category>> {
        TODO("Not yet implemented")
    }

    override fun getExpenseCategories(): Flow<List<Category>> {
        TODO("Not yet implemented")
    }

    override fun getIncomeCategories(): Flow<List<Category>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertCategory(category: Category) {
        dao.insertCategory(category.toEntity())
    }

    override suspend fun updateCategory(category: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCategory(category: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun countByName(
        name: String,
        type: String,
        parentId: String?,
    ): Int {
        TODO("Not yet implemented")
    }

    override suspend fun countTransactionsByCategory(categoryId: String): Int {
        TODO("Not yet implemented")
    }

    override suspend fun countSubCategories(categoryId: String): Int {
        TODO("Not yet implemented")
    }
}