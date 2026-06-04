package com.example.nexcashapplication.feature.dashboard.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.feature.dashboard.presentation.state.TransactionUi

@Composable
fun RecentTransactionSection(
    transactions: List<TransactionUi>,
    onSeeAllClick: () -> Unit = {},
    onItemClick: (Int) -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Transaksi Terakhir",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            TextButton(onClick = onSeeAllClick) {
                Text("Semua")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // LIST TRANSAKSI
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(8.dp)
            ) {

                transactions.forEach { transaction ->

                    TransactionItem(
                        transaction = transaction
                    )

                    Divider(
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}