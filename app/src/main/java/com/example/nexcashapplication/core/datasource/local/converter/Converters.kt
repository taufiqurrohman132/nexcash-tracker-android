package com.example.nexcashapplication.core.datasource.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    // List<String> — converter lama
    @TypeConverter
    fun fromJsonString(value: String?): List<String>? {
        return gson.fromJson(value, object : TypeToken<List<String>>() {}.type)
    }

    @TypeConverter
    fun toJsonString(list: List<String>?): String? {
        return gson.toJson(list)
    }

    // List<Int>
    @TypeConverter
    fun fromJsonInt(value: String?): List<Int> {
        return if (value == null) {
            emptyList()
        } else {
            val type = object : TypeToken<List<Int>>() {}.type
            gson.fromJson(value, type) ?: emptyList()
        }
    }

    @TypeConverter
    fun toJsonInt(list: List<Int>?): String {
        return gson.toJson(list ?: emptyList<Int>())
    }
}