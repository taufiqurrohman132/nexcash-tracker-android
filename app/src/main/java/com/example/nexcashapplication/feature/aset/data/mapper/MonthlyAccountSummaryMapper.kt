package com.example.nexcashapplication.feature.aset.data.mapper

import com.example.nexcashapplication.core.datasource.local.result.MonthlySummaryAccountResult
import com.example.nexcashapplication.feature.aset.domain.model.MonthlySummaryAccount

fun MonthlySummaryAccountResult.toDomain() = MonthlySummaryAccount(
    totalBalance = totalBalance,
    activeAccount = activeAccount,
    monthlyIncome = monthlyIncome,
    monthlyExpense = monthlyExpense,
)