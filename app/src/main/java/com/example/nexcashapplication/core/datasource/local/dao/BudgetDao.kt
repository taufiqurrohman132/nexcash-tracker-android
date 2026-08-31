package com.example.nexcashapplication.core.datasource.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.nexcashapplication.core.datasource.local.model.BudgetEntity
import com.example.nexcashapplication.core.datasource.local.result.CategoryExpenseResult
import com.example.nexcashapplication.core.datasource.local.result.DailyExpenseResult
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets WHERE id = :id")
    suspend fun getById(id: String): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE category_id = :categoryId")
    fun getByCategory(categoryId: String): Flow<List<BudgetEntity>>

    // budget yang "aktif" pada tanggal tertentu:
    // start_date <= date DAN (end_date IS NULL ATAU end_date >= date)
    @Query(
        """
        SELECT * FROM budgets 
        WHERE start_date <= :dateEpoch 
        AND (end_date IS NULL OR end_date >= :dateEpoch)
    """,
    )
    fun getActiveBudgets(dateEpoch: Long): Flow<List<BudgetEntity>>

    @Query(
        """
        SELECT * FROM budgets 
        WHERE category_id = :categoryId
        AND start_date <= :dateEpoch 
        AND (end_date IS NULL OR end_date >= :dateEpoch)
        LIMIT 1
    """,
    )
    fun getActiveBudgetForCategory(categoryId: String, dateEpoch: Long): Flow<BudgetEntity?>

    @Query(
        """
    SELECT * FROM budgets 
    WHERE category_id IN (:categoryIds)
    AND start_date <= :referenceDate
    AND (
        recurrence_type != 'custom' 
        OR (recurrence_type = 'custom' AND end_date >= :referenceDate)
    )
""",
    )
    fun getActiveBudgetsForCategories(
        categoryIds: List<String>,
        referenceDate: Long,
    ): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE recurrence_type = :type")
    fun getByRecurrenceType(type: String): Flow<List<BudgetEntity>>

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteById(id: String)

    // Total pengeluaran harian dalam bulan (untuk grafik tren)
    @Query(
        """
        SELECT 
            strftime('%d', transaction_date/1000, 'unixepoch') as day,
            SUM(amount) as total
        FROM transactions
        WHERE category_id = :categoryId
        AND type = 'expense'
        AND strftime('%m', transaction_date/1000, 'unixepoch') = :month
        AND strftime('%Y', transaction_date/1000, 'unixepoch') = :year
        GROUP BY day
        ORDER BY day ASC
    """,
    )
    fun getDailyExpenseByCategory(
        categoryId: String,
        month: String,
        year: String,
    ): Flow<List<DailyExpenseResult>>

    // Pengeluaran per subkategori (rincian sub-kategori)
    @Query(
        """
        SELECT 
            t.category_id,
            SUM(t.amount) as total
        FROM transactions t
        INNER JOIN categories c ON t.category_id = c.id
        WHERE c.parent_id = :parentCategoryId
        AND t.type = 'expense'
        AND strftime('%m', t.transaction_date/1000, 'unixepoch') = :month
        AND strftime('%Y', t.transaction_date/1000, 'unixepoch') = :year
        GROUP BY t.category_id
    """,
    )
    fun getExpenseBySubCategory(
        parentCategoryId: String,
        month: String,
        year: String,
    ): Flow<List<CategoryExpenseResult>>

    // CRUD ────────────────────────────────────────────

    // Buat anggaran baru
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    // Edit limit anggaran
    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    // Hapus anggaran
    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)


}
