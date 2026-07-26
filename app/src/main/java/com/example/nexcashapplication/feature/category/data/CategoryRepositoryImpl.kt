package com.example.nexcashapplication.feature.category.data

import com.example.nexcashapplication.core.datasource.local.dao.CategoryDao
import com.example.nexcashapplication.feature.category.data.mapper.toDomain
import com.example.nexcashapplication.feature.category.data.mapper.toEntity
import com.example.nexcashapplication.feature.category.domain.model.Category
import com.example.nexcashapplication.feature.category.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    val dao: CategoryDao,
) : CategoryRepository {
    override suspend fun getCategoryById(id: String): Category? {
        return dao.getCategoryById(id)?.toDomain()
    }

//    override suspend fun getAllCategory(): Flow<List<Category?>> {
//        retur
//    }

    override fun searchCategories(
        query: String,
        type: String,
    ): Flow<List<Category>> {
        return dao.searchCategories(
            query = query,
            type = type,
        ).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getExpenseCategories(): Flow<List<Category>> {
        return dao.getExpenseCategories().map { list -> list.map { it.toDomain() } }
    }

    override fun getIncomeCategories(): Flow<List<Category>> {
        return dao.getIncomeCategories().map { list -> list.map { it.toDomain() } }

    }

    override suspend fun insertCategory(category: Category) {
        dao.insertCategory(category.toEntity())
    }

    override suspend fun updateCategory(category: Category) {
        dao.updateCategory(category.toEntity())
    }

    override suspend fun deleteCategory(category: Category) {
        dao.deleteCategory(category.toEntity())
    }

    override suspend fun countByName(
        name: String,
        type: String,
        parentId: String?,
    ): Int {
        return dao.countByName(
            name = name,
            type = type,
            parentId = parentId,
        )
    }

    override suspend fun countTransactionsByCategory(categoryId: String): Int {
        return countTransactionsByCategory(
            categoryId,
        )
    }

    override suspend fun countSubCategories(categoryId: String): Int {
        return countSubCategories(
            categoryId,
        )
    }
}