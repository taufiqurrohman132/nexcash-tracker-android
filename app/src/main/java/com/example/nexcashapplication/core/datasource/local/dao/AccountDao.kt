package com.example.nexcashapplication.core.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.nexcashapplication.core.datasource.local.model.AccountEntity
import com.example.nexcashapplication.core.datasource.local.result.MonthlySummaryAccountResult
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    // Dashboard: total saldo semua rekening (dijumlah)
    @Query("SELECT COALESCE(SUM(current_balance), 0) FROM accounts")
    fun getTotalBalance(): Flow<Long>

    // Dashboard: ambil akun/aset default
    @Query("SELECT * FROM accounts WHERE is_active = 1 LIMIT 1")
    fun getDefaultAccount(): Flow<AccountEntity?>

    // List semua akun/aset
    @Query("SELECT * FROM accounts ORDER BY is_active DESC")
    fun getAllAccounts(): Flow<List<AccountEntity>>

    @Query(
        """
    SELECT
        (SELECT COALESCE(SUM(current_balance), 0) FROM accounts WHERE is_active = 1) AS totalBalance,
        (SELECT COUNT(*) FROM accounts WHERE is_active = 1) AS activeAccount,
        (SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'income' AND strftime('%Y-%m', transaction_date) = strftime('%Y-%m', 'now')) AS monthlyIncome,
        (SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'expense' AND strftime('%Y-%m', transaction_date) = strftime('%Y-%m', 'now')) AS monthlyExpense
""",
    )
    fun getMonthlySummary(): Flow<MonthlySummaryAccountResult>

    @Query("SELECT * FROM accounts WHERE id = :accountId")
    fun getAccountById(accountId: String): Flow<AccountEntity>
}
