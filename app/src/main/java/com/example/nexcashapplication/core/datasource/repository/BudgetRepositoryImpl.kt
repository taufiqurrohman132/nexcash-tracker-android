package com.example.nexcashapplication.core.datasource.repository

import com.example.nexcashapplication.core.common.toEpochMillis
import com.example.nexcashapplication.core.datasource.local.dao.BudgetDao
import com.example.nexcashapplication.core.datasource.local.database.NexCashDatabase
import com.example.nexcashapplication.core.datasource.local.mapper.toDomain
import com.example.nexcashapplication.core.datasource.local.mapper.toEntity
import com.example.nexcashapplication.core.datasource.local.result.CategoryExpenseResult
import com.example.nexcashapplication.core.datasource.local.result.DailyExpenseResult
import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.repository.BudgetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao,
    private val db: NexCashDatabase,
) : BudgetRepository {
    override suspend fun getById(id: String): Budget? {
        return budgetDao.getById(id)?.toDomain()
    }

    override fun getByCategory(categoryId: String): Flow<List<Budget>> {
        return budgetDao.getByCategory(categoryId)
                .map { list -> list.map { it.toDomain() } }
                .flowOn(Dispatchers.Default)
    }

    override fun getActiveBudgets(date: LocalDate): Flow<List<Budget>> {
        return budgetDao.getActiveBudgets(date.toEpochMillis())
                .map { list -> list.map { it.toDomain() } }
                .flowOn(Dispatchers.Default)
    }

    override fun getActiveBudgetForCategory(
        categoryId: String,
        date: LocalDate,
    ): Flow<Budget?> {
        return budgetDao.getActiveBudgetForCategory(categoryId, date.toEpochMillis())
                .map { it?.toDomain() }
                .flowOn(Dispatchers.Default)
    }

    override fun getActiveBudgetsForCategories(
        categoryIds: List<String>,
        referenceDate: Long,
    ): Flow<List<Budget>> {
        return budgetDao.getActiveBudgetsForCategories(categoryIds, referenceDate)
                .map { list -> list.map { it.toDomain() } }
                .flowOn(Dispatchers.Default)
    }

    override fun getByRecurrenceType(type: String): Flow<List<Budget>> {
        return budgetDao.getByRecurrenceType(type)
                .map { list -> list.map { it.toDomain() } }
                .flowOn(Dispatchers.Default)
    }

    override suspend fun deleteById(id: String) {
        budgetDao.deleteById(id)
    }

    override fun getDailyExpenseByCategory(
        categoryId: String,
        monthStart: Long,
        monthEnd: Long,
    ): Flow<List<DailyExpenseResult>> {
        return budgetDao.getDailyExpenseByCategory(categoryId, monthStart, monthEnd)
    }

    override fun getBudgetsByMonth(monthStart: Long, monthEnd: Long): Flow<List<Budget>> {
        return budgetDao.getBudgetsForMonth(monthStart, monthEnd)
                .map { list -> list.map { it.toDomain() } }
                .flowOn(Dispatchers.Default)
    }

    override fun getTotalBudgetLimit(monthStart: Long, monthEnd: Long): Flow<Long> {
        return budgetDao.getTotalBudgetForMonth(monthStart = monthStart, monthEnd = monthEnd)
    }

    override fun getExpenseBySubCategory(
        parentCategoryId: String,
        monthStart: Long,
        monthEnd: Long,
    ): Flow<List<CategoryExpenseResult>> {
        return budgetDao.getExpenseBySubCategory(parentCategoryId, monthStart, monthEnd)
    }

    override suspend fun getSeriesRoots(): List<Budget> {
        return budgetDao.getSeriesRoots().map { it.toDomain() }
    }

    override suspend fun getLastBudgetInSeries(groupId: String): Budget? {
        return budgetDao.getLastBudgetInSeries(groupId)?.toDomain()
    }

    override suspend fun insertBudgetIfAbsent(budget: Budget): Boolean {
        return budgetDao.insertBudgetIfAbsent(budget.toEntity()) != -1L
    }

    override suspend fun insertBudget(budget: Budget) {
        budgetDao.insertBudget(budget.toEntity())
    }

    override suspend fun updateBudget(budget: Budget) {
        budgetDao.updateBudget(budget.toEntity())
    }

    override suspend fun deleteBudget(budget: Budget) {
        budgetDao.deleteBudget(budget.toEntity())
    }

}