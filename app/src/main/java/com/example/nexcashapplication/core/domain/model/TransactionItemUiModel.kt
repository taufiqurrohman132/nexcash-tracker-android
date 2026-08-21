package com.example.nexcashapplication.core.domain.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType

data class TransactionItemUiModel(
    val id: String?,
    val name: String,
    val time: String,       // "10:45"
    val amount: Long,
    val typeTransaction: TransactionType,
    val category: String,
    val account: String,
    val icon: ImageVector, // icon kategori
    val iconColor: Color,
)
