package com.example.nexcashapplication.core.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.nexcashapplication.core.datasource.local.model.TransactionEntity
import com.example.nexcashapplication.core.datasource.local.model.TransactionWithDetailsEntity
import com.example.nexcashapplication.core.datasource.local.result.CategoryExpenseResult
import com.example.nexcashapplication.core.datasource.local.result.DailyTotalResult
import com.example.nexcashapplication.core.datasource.local.result.MonthlySummaryResult
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    // dashboard: List transaksi terakhir limit 10-20
    @Query(
        """
        SELECT * FROM transactions
        WHERE category_id = :categoryId
        ORDER BY transaction_date DESC
        LIMIT CASE WHEN :limit < 0 THEN -1 ELSE :limit END
    """,
    )
    fun getRecentTransactionsByCategory(limit: Int = -1, categoryId: String?): Flow<List<TransactionWithDetailsEntity>>

    @Query(
        """
        SELECT * FROM transactions 
        WHERE strftime('%Y-%m', transaction_date / 1000, 'unixepoch') = :yearMonth
        ORDER BY transaction_date DESC
    """,
    )
    fun getTransactionsByMonth(yearMonth: String): Flow<List<TransactionEntity>>

    // baru — khusus buat nampilin list dengan detail akun/kategori
    @Transaction
    @Query("SELECT * FROM transactions WHERE deleted_at IS NULL ORDER BY transaction_date DESC")
    fun getAllTransactionWithDetails(): Flow<List<TransactionWithDetailsEntity>>

    @Transaction
    @Query(
        """
        SELECT * FROM transactions 
WHERE deleted_at IS NULL AND strftime('%Y-%m', transaction_date / 1000, 'unixepoch') = :yearMonth 
AND (:categoryFilterId IS NULL OR category_id = :categoryFilterId)
ORDER BY transaction_date DESC
        """,
    )
    fun getAllTransactionWithDetailsByMonth(
        yearMonth: String,
        categoryFilterId: String?,
    ): Flow<List<TransactionWithDetailsEntity>>

    @Transaction
    @Query(
        """
            SELECT * FROM transactions WHERE id = :id
        """,
    )
    fun getTransactionWithDetailsById(id: String): Flow<TransactionWithDetailsEntity?>

    @Query(
        """
            SELECT COALESCE(SUM(amount), 0) FROM transactions
            WHERE category_id = :categoryId
            AND transaction_date BETWEEN :periodStart AND :periodEnd
        """,
    )
    fun getUsedAmountByCategory(categoryId: String, periodStart: Long, periodEnd: Long): Flow<Long>

    // dashboard: total bulan ini
    @Query(
        """
    SELECT 
        COALESCE(SUM(CASE WHEN type = 'income' THEN amount ELSE 0 END), 0) AS totalPemasukan,
        COALESCE(SUM(CASE WHEN type = 'expense' THEN amount ELSE 0 END), 0) AS totalPengeluaran,
        COALESCE(SUM(CASE WHEN type = 'income' THEN amount ELSE 0 END), 0) 
        - COALESCE(SUM(CASE WHEN type = 'expense' THEN amount ELSE 0 END), 0) AS totalSaldo
    FROM transactions
    WHERE strftime('%Y-%m', transaction_date/1000, 'unixepoch') = :yearMonth
""",
    )
    fun getMonthlySummary(yearMonth: String): Flow<MonthlySummaryResult>

    @Query(
        """
        SELECT COALESCE(SUM(amount), 0) FROM transactions
        WHERE type = :type
        AND strftime('%Y-%m', transaction_date) = :monthYear
    """,
    )
    fun getTotalForMonth(monthYear: String, type: String): Flow<Long>

    // Dashboard: total pengeluaran per kategori (untuk widget anggaran)
    @Query(
        """
        SELECT category_id, SUM(amount) as total 
        FROM transactions 
        WHERE type = 'expense'
        AND strftime('%m', transaction_date/1000, 'unixepoch') = :month
        AND strftime('%Y', transaction_date/1000, 'unixepoch') = :year
        GROUP BY category_id
    """,
    )
    fun getExpenseGroupedByCategory(month: String, year: String): Flow<List<CategoryExpenseResult>>

    @Query(
        """
            SELECT COUNT(*) FROM transactions
            WHERE account_id = :accountId
        """,
    )
    fun getCountTransactionsByAccount(accountId: String): Flow<Int>

    @Query("SELECT * FROM transactions WHERE category_id IN (:categoryIds)")
    fun getTransactionsForCategories(categoryIds: List<String>): Flow<List<TransactionEntity>>

    @Query(
        """
                SELECT 
            strftime('%w', datetime(transaction_date / 1000, 'unixepoch', 'localtime')) as dayOfWeek,
            SUM(amount) as total
        FROM transactions
        WHERE transaction_date >= :startDate
        AND transaction_date <= :endDate
        AND type = :type
        AND category_id = :categoryId
        GROUP BY dayOfWeek
        ORDER BY dayOfWeek ASC
        """,
    )
    fun getWeeklyTrend(
        startDate: Long,
        endDate: Long,
        type: String? = null,
        categoryId: String? = null,
    ): Flow<List<DailyTotalResult>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionByIdOnce(id: String): TransactionEntity?


    @Query(
        """
        SELECT * FROM transactions 
        WHERE transaction_date BETWEEN :startOfMonth AND :endOfMonth
        ORDER BY transaction_date DESC
    """,
    )
    fun getTransactionsInRange(startOfMonth: Long, endOfMonth: Long): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT COALESCE(SUM(amount), 0) FROM transactions
        WHERE type = 'expense' AND transaction_date BETWEEN :startOfMonth AND :endOfMonth
    """,
    )
    fun getTotalExpenseInRange(startOfMonth: Long, endOfMonth: Long): Flow<Long>

    @Query(
        """
        SELECT COALESCE(SUM(amount), 0) FROM transactions
        WHERE type = 'income' AND transaction_date BETWEEN :startOfMonth AND :endOfMonth
    """,
    )
    fun getTotalIncomeInRange(startOfMonth: Long, endOfMonth: Long): Flow<Double>

    @Query(
        """
        SELECT category_id, SUM(amount) as total FROM transactions
        WHERE type = 'expense' AND transaction_date BETWEEN :startOfMonth AND :endOfMonth
        GROUP BY category_id
    """,
    )
    fun getExpenseByCategoryInRange(
        startOfMonth: Long, endOfMonth: Long,
    ): Flow<List<CategoryExpenseResult>>

    // Search transaksi (search bar di list)
    @Query(
        """
        SELECT * FROM transactions 
        WHERE title LIKE '%' || :query || '%' COLLATE NOCASE
    """,
    )
    fun searchTransactions(query: String): Flow<List<TransactionWithDetailsEntity>>


    // Insert transaksi baru (dari FAB +)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    // Update transaksi
    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    // Delete transaksi
    @Query("UPDATE transactions SET  deleted_at = :timestamp WHERE id = :id")
    suspend fun deleteTransaction(id: String, timestamp: Long)


}
