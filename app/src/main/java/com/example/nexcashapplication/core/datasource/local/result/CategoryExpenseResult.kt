package com.example.nexcashapplication.core.datasource.local.result

import androidx.room.ColumnInfo

data class CategoryExpenseResult(
    @ColumnInfo(name = "category_id")
    val categoryId: String,
    val total: Long
)
