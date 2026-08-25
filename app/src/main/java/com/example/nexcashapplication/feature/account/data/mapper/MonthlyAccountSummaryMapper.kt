package com.example.nexcashapplication.feature.account.data.mapper

import com.example.nexcashapplication.core.datasource.local.result.MonthlySummaryAccountResult
import com.example.nexcashapplication.feature.account.domain.model.MonthlySummaryAccount

fun MonthlySummaryAccountResult.toDomain() = MonthlySummaryAccount(
    totalBalance = totalBalance,
    activeAccount = activeAccount,
    monthlyIncome = monthlyIncome,
    monthlyExpense = monthlyExpense,
)