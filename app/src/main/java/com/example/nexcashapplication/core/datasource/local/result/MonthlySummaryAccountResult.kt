package com.example.nexcashapplication.core.datasource.local.result

data class MonthlySummaryAccountResult(
    val totalBalance: Long,
    val activeAccount: Int,
    val monthlyIncome: Long,
    val monthlyExpense: Long,
)