package com.example.nexcashapplication.core.datasource.repository

import com.example.nexcashapplication.core.datasource.local.dao.BudgetDao
import com.example.nexcashapplication.core.datasource.local.database.NexCashDatabase
import com.example.nexcashapplication.core.datasource.local.mapper.toDomain
import com.example.nexcashapplication.core.datasource.local.mapper.toEntity
import com.example.nexcashapplication.core.datasource.local.result.CategoryExpenseResult
import com.example.nexcashapplication.core.datasource.local.result.DailyExpenseResult
import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao,
    private val db: NexCashDatabase,
) : BudgetRepository {
    override suspend fun getById(id: String): Budget? {
        TODO("Not yet implemented")
    }

    override fun getByCategory(categoryId: String): Flow<List<Budget>> {
        TODO("Not yet implemented")
    }

    override fun getActiveBudgets(date: LocalDate): Flow<List<Budget>> {
        TODO("Not yet implemented")
    }

    override suspend fun getActiveBudgetForCategory(
        categoryId: String,
        date: LocalDate,
    ): Budget? {
        return budgetDao.getActiveBudgetForCategory(categoryId, date)?.toDomain()
    }

    override fun getByRecurrenceType(type: String): Flow<List<Budget>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteById(id: String) {
        budgetDao.deleteById(id)
    }

    override fun getDailyExpenseByCategory(
        categoryId: String,
        month: String,
        year: String,
    ): Flow<List<DailyExpenseResult>> {
        TODO("Not yet implemented")
    }

    override fun getExpenseBySubCategory(
        parentCategoryId: String,
        month: String,
        year: String,
    ): Flow<List<CategoryExpenseResult>> {
        TODO("Not yet implemented")
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