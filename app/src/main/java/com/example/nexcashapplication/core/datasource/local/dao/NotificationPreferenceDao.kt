package com.example.nexcashapplication.core.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nexcashapplication.core.datasource.local.model.NotificationPreferenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationPreferenceDao {

    @Query("SELECT * FROM notification_preferences")
    fun observeAll(): Flow<List<NotificationPreferenceEntity>>

    /** Upsert per budget — primary key diisi budgetId, jadi REPLACE menimpa baris lama. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(preference: NotificationPreferenceEntity)

    @Query("DELETE FROM notification_preferences WHERE budget_id = :budgetId")
    suspend fun deleteByBudget(budgetId: String)
}
