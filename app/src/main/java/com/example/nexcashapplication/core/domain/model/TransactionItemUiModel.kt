package com.example.nexcashapplication.core.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class TransactionItemUiModel(
    val id: String,
    val title: String,
    val time: String,       // "10:45"
    val amount: Long,
    val isIncome: Boolean,
    val category: String,
    val account: String,
    val icon: ImageVector,        // icon kategori
)
