package com.example.nexcashapplication.feature.common.extentions

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType

@Composable
fun TransactionType.toAmountColor(): Color = when (this) {
    TransactionType.INCOME -> MaterialTheme.colorScheme.primary
    TransactionType.EXPENSE -> MaterialTheme.colorScheme.error
}