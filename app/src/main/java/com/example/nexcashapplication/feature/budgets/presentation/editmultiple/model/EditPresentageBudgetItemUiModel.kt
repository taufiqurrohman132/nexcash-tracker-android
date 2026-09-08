package com.example.nexcashapplication.feature.budgets.presentation.editmultiple.model

import androidx.compose.ui.graphics.Color

data class EditPresentageBudgetItemUiModel(
    val id: String,
    val name: String,
    val percentage: Float,
    val color: Color,
    val totalBudget: Long,
)