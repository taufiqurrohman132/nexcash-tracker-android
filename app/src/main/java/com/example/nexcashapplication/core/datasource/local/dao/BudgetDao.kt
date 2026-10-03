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
        AND (end_date >= :dateEpoch)
    """,
    )
    fun getActiveBudgets(dateEpoch: Long): Flow<List<BudgetEntity>>

    @Query(
        """
        SELECT * FROM budgets 
        WHERE category_id = :categoryId
        AND start_date <= :dateEpoch 
        AND end_date >= :dateEpoch
        ORDER BY start_date DESC
        LIMIT 1
    """,
    )
    fun getActiveBudgetForCategory(categoryId: String, dateEpoch: Long): Flow<BudgetEntity?>

    @Query(
        """
    SELECT * FROM budgets 
    WHERE category_id IN (:categoryIds)
    AND start_date <= :referenceDate
    AND end_date >= :referenceDate
    ORDER BY start_date ASC
""",
    )
    fun getActiveBudgetsForCategories(
        categoryIds: List<String>,
        referenceDate: Long,
    ): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE period_type = :type")
    fun getByRecurrenceType(type: String): Flow<List<BudgetEntity>>

    // Semua root seri: baris yang recurring_group_id menunjuk ke id-nya sendiri.
    // Dipakai EnsureUpToDateBudgetsUseCase untuk generate periode berikutnya.
    @Query("SELECT * FROM budgets WHERE recurring_group_id = id")
    suspend fun getSeriesRoots(): List<BudgetEntity>


    @Query(
        """
            SELECT * FROM budgets
            WHERE recurring_group_id = :groupId
            ORDER BY start_date DESC 
            LIMIT 1
        """,
    )
    suspend fun getLastBudgetInSeries(groupId: String): BudgetEntity? // buat proses generate row baru

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteById(id: String)

    // Total pengeluaran harian dalam bulan (untuk grafik tren)
    // Rentang epoch, bukan strftime di kolom → index transaction_date bisa dipakai
    @Query(
        """
        SELECT 
            strftime('%d', transaction_date/1000, 'unixepoch') as day,
            SUM(amount) as total
        FROM transactions
        WHERE category_id = :categoryId
        AND type = 'expense'
        AND transaction_date BETWEEN :monthStart AND :monthEnd
        GROUP BY day
        ORDER BY day ASC
    """,
    )
    fun getDailyExpenseByCategory(
        categoryId: String,
        monthStart: Long,
        monthEnd: Long,
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
        AND t.transaction_date BETWEEN :monthStart AND :monthEnd
        GROUP BY t.category_id
    """,
    )
    fun getExpenseBySubCategory(
        parentCategoryId: String,
        monthStart: Long,
        monthEnd: Long,
    ): Flow<List<CategoryExpenseResult>>

    /**
     * Budget yang menabrak window [monthStart]..[monthEnd] untuk keperluan **Statistik**
     * (sisa anggaran + donut alokasi) — hanya kategori **pengeluaran**.
     *
     * Tanpa filter ini, budget kategori pemasukan ikut dijumlahkan dengan total
     * pengeluaran sehingga "sisa anggaran" & "% terpakai" meleset. Kategori pemasukan
     * tidak boleh punya baris budget (lihat `SaveBudgetUseCase`); query ini penjaga
     * terakhir bila baris lama masih ada.
     */
    @Query(
        """
    SELECT b.* FROM budgets b
    INNER JOIN categories c ON c.id = b.category_id
    WHERE c.type = 'expense'
    AND b.start_date <= :monthEnd AND b.end_date >= :monthStart
""",
    )
    fun getBudgetsForMonth(monthStart: Long, monthEnd: Long): Flow<List<BudgetEntity>>

//    @Query(
//        """
//            SELECT SUM(amount) FROM budgets
//        """,
//    )
//    fun getAmmountBudgetForMonth(monthStart: Long, monthEnd: Long): Flow<Long>

    /** Total batas pengeluaran bulan itu — sengaja hanya kategori pengeluaran, samakan dengan [getBudgetsForMonth]. */
    @Query(
        """
    SELECT COALESCE(SUM(b.amount), 0) FROM budgets b
    INNER JOIN categories c ON c.id = b.category_id
    WHERE c.type = 'expense'
    AND b.start_date <= :monthEnd AND b.end_date >= :monthStart
""",
    )
    fun getTotalBudgetForMonth(monthStart: Long, monthEnd: Long): Flow<Long>
    // CRUD ────────────────────────────────────────────

    // Buat anggaran baru
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    // Insert idempoten untuk generate periode berikutnya:
    // unique index (recurring_group_id, start_date) menahan duplikat, bukan menghapus baris lama
    // (REPLACE = DELETE+INSERT → melanggar FK RESTRICT bila baris punya anak).
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBudgetIfAbsent(budget: BudgetEntity): Long

    // Edit limit anggaran
    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    // Hapus anggaran
    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)


}
