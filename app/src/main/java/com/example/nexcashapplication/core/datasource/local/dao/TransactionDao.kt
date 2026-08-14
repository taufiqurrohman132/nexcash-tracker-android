package com.example.nexcashapplication.core.datasource.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.nexcashapplication.core.datasource.local.model.TransactionEntity
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

    // dashboard: total bulan ini
    @Query(
        """
    SELECT 
        COALESCE(SUM(CASE WHEN type = 'income' THEN amount ELSE 0 END), 0) AS totalPemasukan,
        COALESCE(SUM(CASE WHEN type = 'expense' THEN amount ELSE 0 END), 0) AS totalPengeluaran,
        COALESCE(SUM(CASE WHEN type = 'income' THEN amount ELSE 0 END), 0) 
        - COALESCE(SUM(CASE WHEN type = 'expense' THEN amount ELSE 0 END), 0) AS totalSaldo
    FROM transactions
    WHERE strftime('%m', transaction_date/1000, 'unixepoch') = :month
    AND strftime('%Y', transaction_date/1000, 'unixepoch') = :year
""",
    )
    fun getMonthlySummary(month: String, year: String): Flow<MonthlySummaryResult>


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
