package com.example.nexcashapplication.core.datasource.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.nexcashapplication.core.datasource.local.model.CategoryEntity
import com.example.nexcashapplication.core.datasource.local.model.TransactionEntity
import com.example.nexcashapplication.core.datasource.local.model.TransactionWithDetailsEntity
import com.example.nexcashapplication.core.datasource.local.result.CategoryExpenseResult
import com.example.nexcashapplication.core.datasource.local.result.MonthlySummaryResult
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    // dashboard: List transaksi terakhir limit 10-20
    @Query(
        """
        SELECT * FROM transactions
        ORDER BY transaction_date DESC
        LIMIT :limit
    """,
    )
    fun getRecentTransactions(limit: Int = 20): Flow<List<TransactionEntity>>

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
    @Query("""
        SELECT * FROM transactions 
WHERE deleted_at IS NULL AND strftime('%Y-%m', transaction_date / 1000, 'unixepoch') = :yearMonth 
ORDER BY transaction_date DESC
        """)
    fun getAllTransactionWithDetailsByMonth(yearMonth: String): Flow<List<TransactionWithDetailsEntity>>

    @Transaction
    @Query(
        """
            SELECT * FROM transactions WHERE id = :id
        """
    )
    fun getTransactionWithDetailsById(id: String): Flow<TransactionWithDetailsEntity>

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
    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)


}
