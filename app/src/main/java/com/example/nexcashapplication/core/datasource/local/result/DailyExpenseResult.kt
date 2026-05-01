package com.example.nexcashapplication.core.datasource.local.result

// Untuk grafik tren harian
data class DailyExpenseResult(
    val day: String,        // "01" sampai "31"
    val total: Long
)