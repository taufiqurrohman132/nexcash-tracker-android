package com.example.nexcashapplication.core.datasource.local.result

data class DailyTotalResult(
    val dayOfWeek: Int, // 0=Minggu, 1=Sen, dst
    val total: Long,
)