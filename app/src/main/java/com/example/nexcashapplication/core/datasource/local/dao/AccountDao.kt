package com.example.nexcashapplication.core.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

    // Ringkasan bulan berjalan untuk layar Asset.
    // Pakai rentang epoch (bukan strftime) karena:
    //  1) strftime di kolom tidak bisa pakai index -> full table scan tiap emisi;
    //  2) bug satuan: transaction_date itu epoch MILLIS, tapi strftime menerimanya
    //     sebagai DETIK -> hasilnya selalu 0 (monthlyIncome/monthlyExpense = 0,
    //     persentase perubahan bulanan selalu 0.0%).
    @Query(
        """
    SELECT
        (SELECT COALESCE(SUM(current_balance), 0) FROM accounts WHERE is_active = 1) AS totalBalance,
        (SELECT COUNT(*) FROM accounts WHERE is_active = 1) AS activeAccount,
        (SELECT COALESCE(SUM(amount), 0) FROM transactions
            WHERE type = 'income' AND deleted_at IS NULL
            AND transaction_date BETWEEN :startOfMonth AND :endOfMonth) AS monthlyIncome,
        (SELECT COALESCE(SUM(amount), 0) FROM transactions
            WHERE type = 'expense' AND deleted_at IS NULL
            AND transaction_date BETWEEN :startOfMonth AND :endOfMonth) AS monthlyExpense
""",
    )
    fun getMonthlySummary(startOfMonth: Long, endOfMonth: Long): Flow<MonthlySummaryAccountResult>

    @Query("SELECT * FROM accounts WHERE id = :accountId")
    fun getAccountById(accountId: String): Flow<AccountEntity>

    // Tambah account baru
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAccount(account: AccountEntity)

    // Edit account (nama, ikon, warna)
    @Update
    suspend fun updateAccount(account: AccountEntity)

    // delete
    @Query("DELETE FROM accounts WHERE id = :accountId ")
    suspend fun deleteAccountRow(accountId: String)

    @Query("UPDATE accounts SET current_balance = current_balance + :delta WHERE id = :accountId")
    suspend fun adjustBalance(accountId: String, delta: Long)

}
