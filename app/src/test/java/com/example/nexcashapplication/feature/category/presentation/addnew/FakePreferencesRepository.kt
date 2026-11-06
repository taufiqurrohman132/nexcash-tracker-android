package com.example.nexcashapplication.feature.category.presentation.addnew

import androidx.datastore.preferences.core.Preferences
import com.example.nexcashapplication.core.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer

class FakePreferencesRepository : PreferencesRepository {
    private val lists = mutableMapOf<String, MutableStateFlow<List<*>>>()
    private val preferences = mutableMapOf<String, MutableStateFlow<Any?>>()

    @Suppress("UNCHECKED_CAST")
    override fun <T> getList(
        key: Preferences.Key<String>,
        serializer: KSerializer<List<T>>,
    ): Flow<List<T>> {
        val flow = lists.getOrPut(key.name) { MutableStateFlow(emptyList<Any?>()) }
        return flow.map { it as List<T> }
    }

    override suspend fun <T> saveList(
        key: Preferences.Key<String>,
        value: List<T>,
        serializer: KSerializer<List<T>>,
    ) {
        val flow = lists.getOrPut(key.name) { MutableStateFlow(emptyList<Any?>()) }
        flow.value = value
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getPreference(key: Preferences.Key<T>): Flow<T?> {
        val flow = preferences.getOrPut(key.name) { MutableStateFlow(null) }
        return flow.map { it as T? }
    }

    override suspend fun <T> savePreference(key: Preferences.Key<T>, value: T) {
        val flow = preferences.getOrPut(key.name) { MutableStateFlow(null) }
        flow.value = value
    }

    fun <T> seedList(key: Preferences.Key<String>, value: List<T>) {
        val flow = lists.getOrPut(key.name) { MutableStateFlow(emptyList<Any?>()) }
        flow.value = value
    }
}
