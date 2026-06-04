package com.example.nexcashapplication.feature.dashboard.presentation.ui.components

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.feature.dashboard.presentation.state.TransactionUi

@Composable
fun TransactionItem(
    transaction: TransactionUi
) {

    ListItem(
        headlineContent = {
            Text(transaction.title)
        },
        supportingContent = {
            Text(transaction.date)
        },
        trailingContent = {

            Text(
                text =
                if (transaction.isIncome)
                    "+ Rp ${transaction.amount.formatCurrency()}"
                else
                    "- Rp ${transaction.amount.formatCurrency()}",
                color =
                if (transaction.isIncome)
                    Color(0xFF16A34A)
                else
                    Color.Red
            )
        }
    )
}