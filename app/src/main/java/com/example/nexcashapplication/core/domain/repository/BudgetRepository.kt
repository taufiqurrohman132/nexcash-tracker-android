package com.example.nexcashapplication.core.domain.repository

import com.example.nexcashapplication.core.datasource.local.result.CategoryExpenseResult
import com.example.nexcashapplication.core.datasource.local.result.DailyExpenseResult
import com.example.nexcashapplication.core.domain.model.Budget
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface BudgetRepository {

    suspend fun getById(id: String): Budget?
    fun getByCategory(categoryId: String): Flow<List<Budget>>
    fun getActiveBudgets(date: LocalDate): Flow<List<Budget>>
    fun getActiveBudgetForCategory(categoryId: String, date: LocalDate): Flow<Budget?>
    fun getActiveBudgetsForCategories(
        categoryIds: List<String>,
        referenceDate: Long,
    ): Flow<List<Budget>>

    fun getByRecurrenceType(type: String): Flow<List<Budget>>
    suspend fun deleteById(id: String)
    fun getDailyExpenseByCategory(
        categoryId: String,
        month: String,
        year: String,
    ): Flow<List<DailyExpenseResult>>

    fun getBudgetsByMonth(monthStart: Long, monthEnd: Long): Flow<List<Budget>>
    fun getTotalBudgetLimit(monthStart: Long, monthEnd: Long): Flow<Long>

    fun getExpenseBySubCategory(
        parentCategoryId: String,
        month: String,
        year: String,
    ): Flow<List<CategoryExpenseResult>>

    // CRUD ────────────────────────────────────────────
    // Buat anggaran baru
    suspend fun insertBudget(budget: Budget)
    suspend fun updateBudget(budget: Budget)
    suspend fun deleteBudget(budget: Budget)

}