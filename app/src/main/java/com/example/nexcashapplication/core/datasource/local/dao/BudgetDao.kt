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

    // Dashboard widget: sisa anggaran bulan ini
    // LIST SCREEN ─────────────────────────────────────

    // Semua budget bulan ini (untuk progress bar di list)
    @Query("""
        SELECT * FROM budgets 
        WHERE period_month = :month AND period_year = :year
    """)
    fun getBudgetsByMonth(month: Int, year: Int): Flow<List<BudgetEntity>>

    // DETAIL ANGGARAN ─────────────────────────────────

    // Budget spesifik per kategori + bulan
    @Query("""
        SELECT * FROM budgets 
        WHERE category_id = :categoryId 
        AND period_month = :month AND period_year = :year
        LIMIT 1
    """)
    fun getBudgetByCategory(
        categoryId: String,
        month: Int,
        year: Int
    ): Flow<BudgetEntity?>

    // Total pengeluaran harian dalam bulan (untuk grafik tren)
    @Query("""
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
    """)
    fun getDailyExpenseByCategory(
        categoryId: String,
        month: String,
        year: String
    ): Flow<List<DailyExpenseResult>>

    // Pengeluaran per subkategori (rincian sub-kategori)
    @Query("""
        SELECT 
            t.category_id,
            SUM(t.amount) as total
        FROM transactions t
        INNER JOIN categories c ON t.category_id = c.id
        WHERE c.parent_id = :parentCategoryId
        AND type = 'expense'
        AND strftime('%m', transaction_date/1000, 'unixepoch') = :month
        AND strftime('%Y', transaction_date/1000, 'unixepoch') = :year
        GROUP BY t.category_id
    """)
    fun getExpenseBySubCategory(
        parentCategoryId: String,
        month: String,
        year: String
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
