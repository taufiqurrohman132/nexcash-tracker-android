package com.example.nexcashapplication.feature.dashboard.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.core.extension.formatCurrency

@Composable
fun BalanceCard(
    totalBalance: Long,
    income: Long,
    expense: Long,
    isVisible: Boolean,
    onEyeClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp)
    ) {

        Column(
            modifier = Modifier.padding(24.dp)
        ) {

            Text("Total Saldo Anda")

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = if (isVisible)
                    "Rp ${totalBalance.formatCurrency()}"
                else
                    "Rp ••••••••",
                style =
                MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Row {

                SummaryChip(
                    title = "PEMASUKAN",
                    value = income
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                SummaryChip(
                    title = "PENGELUARAN",
                    value = expense
                )
            }
        }
    }
}