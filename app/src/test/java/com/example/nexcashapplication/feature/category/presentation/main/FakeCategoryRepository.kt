package com.example.nexcashapplication.feature.category.presentation.main

import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.CategoryAndSubcategory
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.feature.category.domain.model.CategoryDeleteResolution
import com.example.nexcashapplication.feature.category.domain.model.CategoryName
import com.example.nexcashapplication.feature.category.domain.model.WeeklyTrend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * In-memory [CategoryRepository] untuk unit test (fitur kategori: main & detail).
 * Flow-nya reaktif (MutableStateFlow) supaya `flatMapLatest`/`combine` di ViewModel
 * bekerja seperti Room — seed dulu sebelum membuat ViewModel, atau ubah saat test berjalan.
 *
 * - [seedWeeklyTrend]: isi data tren mingguan (default tetap kosong).
 * - [throwOnLoad]: bila diisi, [getCategoryById] melempar — untuk menguji jalur
 *   `.catch` → `UiState.Error` di DetailCategoryViewModel.
 */
class FakeCategoryRepository : CategoryRepository {

    private val categories = MutableStateFlow<List<Category>>(emptyList())
    private val weeklyTrend = MutableStateFlow(WeeklyTrend(data = emptyList(), labels = emptyList()))

    /** Exception yang dilempar [getCategoryById]; null = normal. */
    var throwOnLoad: Exception? = null

    fun seed(vararg rows: Category) {
        categories.value = categories.value + rows
    }

    /** Isi tren mingguan (7 hari) yang dikembalikan [getWeeklyTrend]. */
    fun seedWeeklyTrend(trend: WeeklyTrend) {
        weeklyTrend.value = trend
    }

    override fun getCategoryById(id: String): Flow<Category?> {
        val error = throwOnLoad
        if (error != null) return flow { throw error }
        return categories.map { list -> list.find { it.id == id } }
    }

    override fun getAllCategory(): Flow<List<Category?>> =
        categories.map { list -> list.map { it } }

    override fun searchCategories(query: String, type: String): Flow<List<Category>> =
        categories.map { list ->
            list.filter {
                it.type.name.lowercase() == type && it.name.contains(query, ignoreCase = true)
            }
        }

    // Meniru query DAO: parent_id IS NULL, urut nama
    override fun getExpenseCategories(): Flow<List<Category>> =
        categories.map { list ->
            list.filter { it.type == TypeCategory.EXPENSE && it.parentId == null }
                .sortedBy { it.name }
        }

    override fun getIncomeCategories(): Flow<List<Category>> =
        categories.map { list ->
            list.filter { it.type == TypeCategory.INCOME && it.parentId == null }
                .sortedBy { it.name }
        }

    override fun getSubCategories(parentId: String): Flow<List<Category>> =
        categories.map { list -> list.filter { it.parentId == parentId } }

    override suspend fun getParentCategories(): List<Category> =
        categories.value.filter { it.parentId == null }

    override suspend fun getParentCategoriesNameByType(type: String): List<CategoryName> =
        emptyList()

    override suspend fun getCategoriesAndSubCategories(): List<CategoryAndSubcategory> =
        emptyList()

    override fun getWeeklyTrend(
        typeTransaction: String?,
        categoryId: String?,
    ): Flow<WeeklyTrend> = weeklyTrend

    override suspend fun insertCategory(category: Category) {
        categories.value = categories.value + category
    }

    override suspend fun updateCategory(category: Category) {
        categories.value = categories.value.map { if (it.id == category.id) category else it }
    }

    override suspend fun deleteCategory(
        categoryId: String,
        resolution: CategoryDeleteResolution,
    ) {
        categories.value = categories.value.filterNot { it.id == categoryId }
    }

    override suspend fun countByName(name: String, type: String, parentId: String?): Int =
        categories.value.count {
            it.name == name && it.type.name.lowercase() == type && it.parentId == parentId
        }

    override suspend fun countTransactionsByCategory(categoryId: String): Int = 0

    override suspend fun countSubCategories(parentId: String): Int =
        categories.value.count { it.parentId == parentId }
}
