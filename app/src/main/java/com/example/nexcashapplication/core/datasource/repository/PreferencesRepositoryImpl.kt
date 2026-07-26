package com.example.nexcashapplication.core.datasource.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.nexcashapplication.core.domain.repository.PreferencesRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : PreferencesRepository {
    override fun <T> getList(
        key: Preferences.Key<String>,
        serializer: KSerializer<List<T>>,
    ): Flow<List<T>> {
        return dataStore.data.map { preferences ->
            preferences[key]?.let { json ->
                try {
                    Json.decodeFromString(serializer, json)
                } catch (e: Exception) {
                    emptyList()
                }
            } ?: emptyList()
        }
    }

    override suspend fun <T> saveList(
        key: Preferences.Key<String>,
        value: List<T>,
        serializer: KSerializer<List<T>>,
    ) {
        dataStore.edit { preferences ->
            preferences[key] = Json.encodeToString(serializer, value)
        }
    }


}