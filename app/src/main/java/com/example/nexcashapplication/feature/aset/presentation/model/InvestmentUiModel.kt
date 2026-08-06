package com.example.nexcashapplication.feature.aset.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
data class InvestmentUiModel(
    val id: String,
    val name: String,
    val currentValue: Long,
    val changePercent: Double,
//    val icon: InvestmentIcon,
)