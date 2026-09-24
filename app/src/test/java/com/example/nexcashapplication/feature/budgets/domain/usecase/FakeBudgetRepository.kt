package com.example.nexcashapplication.feature.budgets.domain.usecase

import com.example.nexcashapplication.core.common.toEpochMillis
import com.example.nexcashapplication.core.datasource.local.result.CategoryExpenseResult
import com.example.nexcashapplication.core.datasource.local.result.DailyExpenseResult
import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate
import java.util.UUID

/**
 * In-memory [BudgetRepository] untuk unit test.
 * Meniru perilaku mapper + Room:
 * - insert memberi id segar dan self-reference root (recurringGroupId = id)
 * - insertBudgetIfAbsent meniru OnConflictStrategy.IGNORE pada (recurring_group_id, start_date)
 * - urutan delete dicatat supaya test bisa memastikan anak dihapus sebelum root (FK RESTRICT)
 */
class FakeBudgetRepository : BudgetRepository {

    private val budgets = mutableListOf<Budget>()

    /** Urutan id yang pernah dihapus (untuk assert FK: anak sebelum root). */
    val deletedIds = mutableListOf<String>()

    val stored: List<Budget>
        get() = budgets.toList()

    fun seed(vararg rows: Budget) {
        rows.forEach { budgets += materialize(it) }
    }

    private fun materialize(budget: Budget): Budget {
        val rowId = budget.id ?: UUID.randomUUID().toString()
        return budget.copy(id = rowId, recurringGroupId = budget.recurringGroupId ?: rowId)
    }

    override suspend fun getById(id: String): Budget? = budgets.find { it.id == id }

    override fun getByCategory(categoryId: String): Flow<List<Budget>> = flow {
        emit(budgets.filter { it.categoryId == categoryId })
    }

    override fun getActiveBudgets(date: LocalDate): Flow<List<Budget>> = flow {
        emit(budgets.filter { !it.startDate.isAfter(date) && !it.endDate.isBefore(date) })
    }

    override fun getActiveBudgetForCategory(
        categoryId: String,
        date: LocalDate,
    ): Flow<Budget?> = flow {
        emit(
            budgets
                .filter {
                    it.categoryId == categoryId &&
                            !it.startDate.isAfter(date) && !it.endDate.isBefore(date)
                }
                .maxByOrNull { it.startDate },
        )
    }

    override fun getActiveBudgetsForCategories(
        categoryIds: List<String>,
        referenceDate: Long,
    ): Flow<List<Budget>> = flow {
        emit(
            budgets.filter {
                it.categoryId in categoryIds &&
                        it.startDate.toEpochMillis() <= referenceDate &&
                        it.endDate.toEpochMillis() >= referenceDate
            },
        )
    }

    override fun getByRecurrenceType(type: String): Flow<List<Budget>> = flow {
        emit(budgets.filter { it.recurrenceType.name == type })
    }

    override suspend fun deleteById(id: String) {
        budgets.removeAll { it.id == id }
        deletedIds += id
    }

    override fun getDailyExpenseByCategory(
        categoryId: String,
        month: String,
        year: String,
    ): Flow<List<DailyExpenseResult>> = flowOf(emptyList())

    override fun getBudgetsByMonth(monthStart: Long, monthEnd: Long): Flow<List<Budget>> = flow {
        emit(
            budgets.filter {
                it.startDate.toEpochMillis() <= monthEnd && it.endDate.toEpochMillis() >= monthStart
            },
        )
    }

    override fun getTotalBudgetLimit(monthStart: Long, monthEnd: Long): Flow<Long> = flowOf(0L)

    override fun getExpenseBySubCategory(
        parentCategoryId: String,
        month: String,
        year: String,
    ): Flow<List<CategoryExpenseResult>> = flowOf(emptyList())

    override suspend fun getSeriesRoots(): List<Budget> =
        budgets.filter { it.isSeriesRoot }

    override suspend fun getLastBudgetInSeries(groupId: String): Budget? =
        budgets
            .filter { (it.recurringGroupId ?: it.id) == groupId }
            .maxByOrNull { it.startDate }

    override suspend fun insertBudgetIfAbsent(budget: Budget): Boolean {
        val row = materialize(budget)
        val duplicate = budgets.any {
            (it.recurringGroupId ?: it.id) == row.recurringGroupId && it.startDate == row.startDate
        }
        if (duplicate) return false
        budgets += row
        return true
    }

    override suspend fun insertBudget(budget: Budget) {
        budgets += materialize(budget)
    }

    override suspend fun updateBudget(budget: Budget) {
        val idx = budgets.indexOfFirst { it.id == budget.id }
        if (idx >= 0) budgets[idx] = budget
    }

    override suspend fun deleteBudget(budget: Budget) {
        budgets.removeAll { it.id == budget.id }
        deletedIds += budget.id ?: return
    }
}
