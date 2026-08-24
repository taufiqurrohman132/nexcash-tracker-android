package com.example.nexcashapplication.core.datasource.repository

import android.util.Log
import androidx.room.withTransaction
import com.example.nexcashapplication.core.datasource.local.dao.CategoryDao
import com.example.nexcashapplication.core.datasource.local.dao.TransactionDao
import com.example.nexcashapplication.core.datasource.local.database.NexCashDatabase
import com.example.nexcashapplication.core.datasource.local.mapper.toDomain
import com.example.nexcashapplication.core.datasource.local.mapper.toEntity
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.CategoryAndSubcategory
import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.feature.category.domain.model.CategoryDeleteResolution
import com.example.nexcashapplication.feature.category.domain.model.CategoryName
import com.example.nexcashapplication.feature.category.domain.model.WeeklyTrend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
    private val db: NexCashDatabase,
) : CategoryRepository {
    override fun getCategoryById(id: String): Flow<Category?> {
        return categoryDao.getCategoryById(id).map { it?.toDomain() }
    }

    override fun getAllCategory(): Flow<List<Category?>> {
        return categoryDao.getAllCategories().map { list -> list.map { it?.toDomain() } }
    }

    override fun searchCategories(
        query: String,
        type: String,
    ): Flow<List<Category>> {
        return categoryDao.searchCategories(
            query = query,
            type = type,
        ).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getExpenseCategories(): Flow<List<Category>> {
        return categoryDao.getExpenseCategories().map { list -> list.map { it.toDomain() } }
    }

    override fun getIncomeCategories(): Flow<List<Category>> {
        return categoryDao.getIncomeCategories().map { list -> list.map { it.toDomain() } }

    }

    override fun getSubCategories(parentId: String): Flow<List<Category>> {
        return categoryDao.getSubCategories(parentId).map { list ->
            Log.d("getSubCategories", "loadDetailCategory: list = $list")

            list.map { it.toDomain() }
        }
    }

    override suspend fun getParentCategories(): List<Category> {
        return categoryDao.getParentCategories().map { list -> list.toDomain() }
    }

    override suspend fun getParentCategoriesNameByType(type: String): List<CategoryName> {
        return categoryDao.getParentCategoriesNameByType(type).map { list -> list.toDomain() }
    }

    override suspend fun getCategoriesAndSubCategories(): List<CategoryAndSubcategory> {
        return categoryDao.getCategoriesAndSubCategories().map { list -> list.toDomain() }
    }

    override fun getWeeklyTrend(typeTransaction: String?, categoryId: String?): Flow<WeeklyTrend> {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val startDate = today.minusDays(7)

        val startDateMillis = startDate.atStartOfDay(zone).toInstant().toEpochMilli()
        val endDateMillis = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

        val dayNames = arrayOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab") // index 0 = Minggu

        return transactionDao.getWeeklyTrend(
            startDate = startDateMillis,
            endDate = endDateMillis,
            type = typeTransaction,
            categoryId = categoryId,
        ).map { result ->
            val dates = List(7) { startDate.plusDays(it.toLong()) }
            val dataMap = result.associateBy { it.dayOfWeek }

            WeeklyTrend(
                labels = dates.map { date -> dayNames[date.dayOfWeek.value % 7] },
                data = dates.map { date ->
                    val dow = date.dayOfWeek.value % 7
                    dataMap[dow]?.total?.toFloat() ?: 0f
                },
            )
        }
    }

    override suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(category.toEntity())
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category.toEntity())
    }

    override suspend fun deleteCategory(
        categoryId: String,
        resolution: CategoryDeleteResolution,
    ) {
        db.withTransaction {
            when (resolution) {
                CategoryDeleteResolution.DeleteAllTransactions ->
                    categoryDao.deleteTransactionByCategory(categoryId)

                is CategoryDeleteResolution.MoveToCategory ->
                    categoryDao.reassignTransactions(categoryId, resolution.targetCategoryId)

                CategoryDeleteResolution.Uncategorize ->
                    categoryDao.uncategorizeTransactions(categoryId)
            }
            categoryDao.deleteCategoryRow(categoryId)
        }
    }

    override suspend fun countByName(
        name: String,
        type: String,
        parentId: String?,
    ): Int {
        return categoryDao.countByName(
            name = name,
            type = type,
            parentId = parentId,
        )
    }

    override suspend fun countTransactionsByCategory(categoryId: String): Int {
        return categoryDao.countTransactionsByCategory(
            categoryId,
        )
    }

    override suspend fun countSubCategories(categoryId: String): Int {
        return categoryDao.countSubCategories(
            categoryId,
        )
    }
}