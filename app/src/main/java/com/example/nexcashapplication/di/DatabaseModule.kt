package com.example.nexcashapplication.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.nexcashapplication.core.datasource.local.dao.AccountDao
import com.example.nexcashapplication.core.datasource.local.dao.BudgetDao
import com.example.nexcashapplication.core.datasource.local.dao.CategoryDao
import com.example.nexcashapplication.core.datasource.local.dao.TransactionDao
import com.example.nexcashapplication.core.datasource.local.database.NexCashDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // v1 → v2: rebuild tabel budgets.
    // Perubahan v1 → v2 yang tidak bisa di-ALTER in-place:
    //   1. recurring_group_id: INTEGER (Long?) → TEXT (String?)
    //   2. FK self-referencing: ON DELETE CASCADE → ON DELETE RESTRICT
    // Ditambah backfill semua root lama (recurring_group_id NULL) menjadi self-reference,
    // supaya unique index (recurring_group_id, start_date) melindungi root dari duplikat.
    // Karena tipe kolom & aksi FK tak bisa diubah via ALTER, tabel dibangun ulang:
    // salin data ke staging (tanpa FK) → drop lama → buat ulang sesuai entity → pindahkan data →
    // buat ulang indeks. SELECT-nya adaptif terhadap bentuk skema v1 lama di perangkat user.
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // 1. Deteksi kolom yang benar-benar ada pada skema v1 perangkat.
            val existing = mutableSetOf<String>()
            db.query("PRAGMA table_info(budgets)").use { cursor ->
                val nameIndex = cursor.getColumnIndexOrThrow("name")
                while (cursor.moveToNext()) existing += cursor.getString(nameIndex)
            }

            // Ekspresi SELECT adaptif: skema sangat lama mungkin berbeda nama/tipe kolom.
            val groupIdExpr =
                if ("recurring_group_id" in existing) "COALESCE(CAST(recurring_group_id AS TEXT), id)"
                else "id" // kolom belum ada → semua root langsung jadi self-reference
            val isRecurringExpr = if ("is_recurring" in existing) "is_recurring" else "1"
            val periodTypeExpr = when {
                "period_type" in existing -> "period_type"
                "recurrence_type" in existing -> "recurrence_type" // nama kolom sebelum refactor
                else -> "'MONTHLY'"
            }
            val createdAtExpr = if ("created_at" in existing) "created_at" else "0"
            // end_date lama boleh NULL = berlaku terus → beri sentinel jauh (2100-01-01).
            val endDateExpr =
                if ("end_date" in existing) "COALESCE(end_date, 4102444800000)" else "4102444800000"
            val amountExpr = if ("amount" in existing) "COALESCE(amount, 0)" else "0"

            // 2. Salin data ke staging TANPA foreign key (self-referencing aman di segala keadaan).
            db.execSQL("DROP TABLE IF EXISTS budgets_stage_v2")
            db.execSQL(
                """
                CREATE TABLE budgets_stage_v2 (
                    id TEXT NOT NULL PRIMARY KEY,
                    category_id TEXT NOT NULL,
                    start_date INTEGER NOT NULL,
                    end_date INTEGER NOT NULL,
                    amount INTEGER NOT NULL,
                    recurring_group_id TEXT,
                    is_recurring INTEGER NOT NULL,
                    period_type TEXT NOT NULL,
                    created_at INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO budgets_stage_v2 (
                    id, category_id, start_date, end_date, amount,
                    recurring_group_id, is_recurring, period_type, created_at
                )
                SELECT
                    id, category_id, start_date, $endDateExpr, $amountExpr,
                    $groupIdExpr, $isRecurringExpr, $periodTypeExpr, $createdAtExpr
                FROM budgets
                """.trimIndent()
            )

            // 3. Ganti tabel dengan skema persis entity v2.
            //    Tabel lama tidak direferensikan tabel lain; self-FK lama CASCADE membersihkan
            //    barisnya sendiri saat implicit DELETE dari DROP (atau FK memang sedang OFF).
            db.execSQL("DROP TABLE budgets")
            db.execSQL(
                """
                CREATE TABLE budgets (
                    id TEXT NOT NULL,
                    category_id TEXT NOT NULL,
                    start_date INTEGER NOT NULL,
                    end_date INTEGER NOT NULL,
                    amount INTEGER NOT NULL,
                    recurring_group_id TEXT,
                    is_recurring INTEGER NOT NULL,
                    period_type TEXT NOT NULL,
                    created_at INTEGER NOT NULL,
                    PRIMARY KEY(id),
                    FOREIGN KEY(category_id) REFERENCES categories(id) ON DELETE RESTRICT,
                    FOREIGN KEY(recurring_group_id) REFERENCES budgets(id) ON DELETE RESTRICT
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO budgets (
                    id, category_id, start_date, end_date, amount,
                    recurring_group_id, is_recurring, period_type, created_at
                )
                SELECT
                    id, category_id, start_date, end_date, amount,
                    recurring_group_id, is_recurring, period_type, created_at
                FROM budgets_stage_v2
                """.trimIndent()
            )
            db.execSQL("DROP TABLE budgets_stage_v2")

            // 4. Indeks sesuai deklarasi entity (nama mengikuti konvensi Room:
            //    index_<tabel>_<kolom1>_<kolom2>...).
            db.execSQL("CREATE INDEX IF NOT EXISTS index_budgets_category_id ON budgets(category_id)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_budgets_recurring_group_id ON budgets(recurring_group_id)")
            db.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS index_budgets_recurring_group_id_start_date " +
                    "ON budgets(recurring_group_id, start_date)"
            )
        }
    }

    /**
     * v2 → v3: indeks yang dipakai filter bulanan supaya memakai index,
     * bukan full table scan.
     * - `transactions.transaction_date`: daftar transaksi, ringkasan bulanan,
     *   ringkasan bulan-berjalan di layar Asset.
     * - `budgets.start_date` / `budgets.end_date`: budget per bulan (statistik,
     *   budget) — query `start_date <= :x AND end_date >= :y`.
     * Nama index harus persis konvensi Room (index_<tabel>_<kolom>) supaya cocok
     * dengan TableInfo yang divalidasi Room saat membuka database.
     * Migrasi ini belum pernah dieksekusi di build mana pun, jadi semua indeks
     * digabung dalam satu versi.
     */
    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS index_transactions_transaction_date " +
                        "ON transactions(transaction_date)",
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_budgets_start_date ON budgets(start_date)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_budgets_end_date ON budgets(end_date)")
        }
    }

    @Provides
    @Singleton
    fun provideNexCashDatabase(@ApplicationContext context: Context): NexCashDatabase =
        Room.databaseBuilder(
            context,
            NexCashDatabase::class.java,
            "nex_cash_db",
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()

    @Provides
    @Singleton
    fun provideCategoryDao(database: NexCashDatabase): CategoryDao =
        database.categoryDao()

    @Provides
    @Singleton
    fun provideAccountDao(database: NexCashDatabase): AccountDao =
        database.accountDao()

    @Provides
    @Singleton
    fun provideTransactionDao(database: NexCashDatabase): TransactionDao =
        database.transactionDao()

    @Provides
    @Singleton
    fun provideBudgetDao(database: NexCashDatabase): BudgetDao =
        database.budgetDao()


}