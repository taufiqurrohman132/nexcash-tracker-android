package com.example.nexcashapplication.core.datasource.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.nexcashapplication.core.datasource.local.model.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    // LIST SCREEN ─────────────────────────────────────

    // Semua kategori pengeluaran (tab Pengeluaran)
    @Query(
        """
        SELECT * FROM categories 
        WHERE type = 'expense' AND parent_id IS NULL
        ORDER BY name ASC
    """,
    )
    fun getExpenseCategories(): Flow<List<CategoryEntity>>

    // Semua kategori pemasukan (tab Pemasukan)
    @Query(
        """
        SELECT * FROM categories 
        WHERE type = 'income' AND parent_id IS NULL
        ORDER BY name ASC
    """,
    )
    fun getIncomeCategories(): Flow<List<CategoryEntity>>

    // Search kategori (search bar di list)
    @Query(
        """
        SELECT * FROM categories 
        WHERE name LIKE '%' || :query || '%'
        AND type = :type
    """,
    )
    fun searchCategories(query: String, type: String): Flow<List<CategoryEntity>>

    // DETAIL KATEGORI ─────────────────────────────────

    // Ambil satu kategori by ID
    @Query("SELECT * FROM categories WHERE id = :id")
    fun getCategoryById(id: String): Flow<CategoryEntity?>

    // Ambil subkategori dari kategori induk
    @Query(
        """
        SELECT * FROM categories 
        WHERE parent_id = :parentId
        ORDER BY name ASC
    """,
    )
    fun getSubCategories(parentId: String): Flow<List<CategoryEntity>>

    // CRUD ────────────────────────────────────────────

    // Tambah kategori baru
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCategory(category: CategoryEntity)

    // Edit kategori (nama, ikon, warna)
    @Update
    suspend fun updateCategory(category: CategoryEntity)

    // Hapus kategori
    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    // Cek duplikat nama sebelum insert
    @Query(
        """
        SELECT COUNT(*) FROM categories 
        WHERE name = :name AND type = :type AND parent_id IS :parentId
    """,
    )
    suspend fun countByName(name: String, type: String, parentId: String?): Int

    // Cek apakah kategori punya transaksi (sebelum hapus)
    @Query(
        """
        SELECT COUNT(*) FROM transactions 
        WHERE category_id = :categoryId
    """,
    )
    suspend fun countTransactionsByCategory(categoryId: String): Int

    // Cek apakah kategori punya subkategori (sebelum hapus)
    @Query(
        """
        SELECT COUNT(*) FROM categories 
        WHERE parent_id = :categoryId
    """,
    )
    suspend fun countSubCategories(categoryId: String): Int
}
