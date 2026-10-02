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
        monthStart: Long,
        monthEnd: Long,
    ): Flow<List<DailyExpenseResult>>

    fun getBudgetsByMonth(monthStart: Long, monthEnd: Long): Flow<List<Budget>>
    fun getTotalBudgetLimit(monthStart: Long, monthEnd: Long): Flow<Long>

    fun getExpenseBySubCategory(
        parentCategoryId: String,
        monthStart: Long,
        monthEnd: Long,
    ): Flow<List<CategoryExpenseResult>>

    // Seri recurring ─────────────────────────────────
    /** Semua root seri (baris dengan recurring_group_id menunjuk ke id-nya sendiri). */
    suspend fun getSeriesRoots(): List<Budget>

    /** Baris terakhir (start_date terbesar) dalam satu seri, root ikut dihitung. */
    suspend fun getLastBudgetInSeries(groupId: String): Budget?

    /** Insert idempoten: duplikat (recurring_group_id, start_date) diabaikan. True bila benar-benar insert. */
    suspend fun insertBudgetIfAbsent(budget: Budget): Boolean

    // CRUD ────────────────────────────────────────────
    // Buat anggaran baru
    suspend fun insertBudget(budget: Budget)
    suspend fun updateBudget(budget: Budget)
    suspend fun deleteBudget(budget: Budget)

}