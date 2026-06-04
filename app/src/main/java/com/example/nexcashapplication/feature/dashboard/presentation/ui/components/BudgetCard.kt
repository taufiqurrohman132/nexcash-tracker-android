package com.example.nexcashapplication.feature.dashboard.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.core.extension.formatCurrency

@Composable
fun BudgetCard(
    remainingBudget: Long,
    progress: Float,
    onDetailClick: () -> Unit = {}
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Sisa Anggaran",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                TextButton(onClick = onDetailClick) {
                    Text("Lihat Detail")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Periode (optional dari UI kamu: "Oktober 2023")
            Text(
                text = "Oktober 2023",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Remaining budget
            Text(
                text = "Rp ${remainingBudget.formatCurrency()} tersisa",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00C2FF)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50)),
                color = Color(0xFF00C2FF),
                trackColor = Color(0xFFE6F7FF)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // percentage text
            Text(
                text = "${(progress * 100).toInt()}% Terpakai",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF00C2FF),
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tip text
            Text(
                text = "Tip: Pengeluaran makan Anda naik 15% minggu ini.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}