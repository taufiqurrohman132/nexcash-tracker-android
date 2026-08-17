package com.example.nexcashapplication.feature.category.data

import androidx.room.withTransaction
import com.example.nexcashapplication.core.datasource.local.dao.CategoryDao
import com.example.nexcashapplication.core.datasource.local.database.NexCashDatabase
import com.example.nexcashapplication.feature.category.data.mapper.toDomain
import com.example.nexcashapplication.feature.category.data.mapper.toEntity
import com.example.nexcashapplication.feature.category.domain.model.Category
import com.example.nexcashapplication.feature.category.domain.model.CategoryDeleteResolution
import com.example.nexcashapplication.feature.category.domain.model.CategoryName
import com.example.nexcashapplication.feature.category.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    val dao: CategoryDao,
    val db: NexCashDatabase,
) : CategoryRepository {
    override fun getCategoryById(id: String): Flow<Category?> {
        return dao.getCategoryById(id).map { it?.toDomain() }
    }

    override fun getAllCategory(): Flow<List<Category?>> {
        return dao.getAllCategories().map { list -> list.map { it?.toDomain() } }
    }

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

    override fun getSubCategories(parentId: String): Flow<List<Category>> {
        return dao.getSubCategories(parentId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getParentCategories(): List<Category> {
        return dao.getParentCategories().map { list -> list.toDomain() }
    }

    override suspend fun getParentCategoriesNameByType(type: String): List<CategoryName> {
        return dao.getParentCategoriesNameByType(type).map { list -> list.toDomain() }
    }

    override suspend fun insertCategory(category: Category) {
        dao.insertCategory(category.toEntity())
    }

    override suspend fun updateCategory(category: Category) {
        dao.updateCategory(category.toEntity())
    }

    override suspend fun deleteCategory(
        categoryId: String,
        resolution: CategoryDeleteResolution,
    ) {
        db.withTransaction {
            when (resolution) {
                CategoryDeleteResolution.DeleteAllTransactions ->
                    dao.deleteTransactionByCategory(categoryId)

                is CategoryDeleteResolution.MoveToCategory ->
                    dao.reassignTransactions(categoryId, resolution.targetCategoryId)

                CategoryDeleteResolution.Uncategorize ->
                    dao.uncategorizeTransactions(categoryId)
            }
            dao.deleteCategoryRow(categoryId)
        }
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
        return dao.countTransactionsByCategory(
            categoryId,
        )
    }

    override suspend fun countSubCategories(categoryId: String): Int {
        return dao.countSubCategories(
            categoryId,
        )
    }
}