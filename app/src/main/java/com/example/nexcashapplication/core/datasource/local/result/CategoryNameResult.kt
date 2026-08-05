package com.example.nexcashapplication.core.datasource.local.result

import androidx.room.ColumnInfo

data class CategoryNameResult(
    @ColumnInfo(name = "id")
    val categoryId: String,
    val name: String,
    val icon: String,
    val color: String,
)
