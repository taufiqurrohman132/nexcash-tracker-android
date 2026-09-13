package com.example.nexcashapplication.core.domain.repository

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

interface PreferencesRepository {

    fun <T> getList(key: Preferences.Key<String>, serializer: KSerializer<List<T>>): Flow<List<T>>
    suspend fun <T> saveList(
        key: Preferences.Key<String>,
        value: List<T>,
        serializer: KSerializer<List<T>>,
    )

    fun <T> getPreference(
        key: Preferences.Key<T>,
//        serializer: KSerializer<T>,
    ): Flow<T?>

    suspend fun <T> savePreference(
        key: Preferences.Key<T>,
        value: T,
//        serializer: KSerializer<T>,
    )


}