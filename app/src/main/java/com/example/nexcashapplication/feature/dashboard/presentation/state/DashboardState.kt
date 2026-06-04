package com.example.nexcashapplication.feature.dashboard.presentation.state

data class DashboardState (
    val userName: String = "Budi",
    val totalBalance: Long = 14560200,
    val income: Long = 12500000,
    val expense: Long = 3245000,
    val remainingBudget: Long = 1755000,
    val budgetUsedPercentage: Float = 0.65f,
    val transactions: List<TransactionUi> = emptyList(),
    val isBalanceVisible: Boolean = true
)

data class TransactionUi(
    val id: Int,
    val title: String,
    val amount: Long,
    val category: String,
    val date: String,
    val isIncome: Boolean
)