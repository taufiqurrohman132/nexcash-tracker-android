package com.example.nexcashapplication.feature.category.domain.repository

import com.example.nexcashapplication.feature.category.domain.model.Category
import com.example.nexcashapplication.feature.category.domain.model.CategoryDeleteResolution
import com.example.nexcashapplication.feature.category.domain.model.CategoryName
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategoryById(id: String): Flow<Category?>

    fun getAllCategory(): Flow<List<Category?>>
    fun searchCategories(query: String, type: String): Flow<List<Category>>
    fun getExpenseCategories(): Flow<List<Category>>
    fun getIncomeCategories(): Flow<List<Category>>
    fun getSubCategories(parentId: String): Flow<List<Category>>  // ← ini yang kurang
    suspend fun getParentCategories(): List<Category>  // ← ini yang kurang
    suspend fun getParentCategoriesNameByType(type: String): List<CategoryName>  // ← ini yang kurang

    suspend fun insertCategory(category: Category)
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(categoryId: String, resolution: CategoryDeleteResolution)

    suspend fun countByName(name: String, type: String, parentId: String?): Int

    suspend fun countTransactionsByCategory(categoryId: String): Int
    suspend fun countSubCategories(categoryId: String): Int
}