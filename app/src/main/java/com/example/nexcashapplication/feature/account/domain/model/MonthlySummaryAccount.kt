package com.example.nexcashapplication.feature.account.domain.model

data class MonthlySummaryAccount(
    // dari repository — data mentah
    val totalBalance: Long,
    val activeAccount: Int,
    val monthlyIncome: Long,
    val monthlyExpense: Long,
)