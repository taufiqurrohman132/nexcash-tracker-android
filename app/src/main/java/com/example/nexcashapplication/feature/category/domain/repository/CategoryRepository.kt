package com.example.nexcashapplication.feature.category.domain.repository

import com.example.nexcashapplication.feature.category.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun getCategoryById(id: String): Category?

    //    suspend fun getAllCategory(): Flow<List<Category?>>
    fun searchCategories(query: String, type: String): Flow<List<Category>>
    fun getExpenseCategories(): Flow<List<Category>>
    fun getIncomeCategories(): Flow<List<Category>>

    suspend fun insertCategory(category: Category)
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    suspend fun countByName(name: String, type: String, parentId: String?): Int

    suspend fun countTransactionsByCategory(categoryId: String): Int
    suspend fun countSubCategories(categoryId: String): Int
}