package com.example.nexcashapplication.feature.dashboard.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.ui.theme.LightCyan
import com.example.nexcashapplication.ui.theme.PrimaryCyan

@Composable
fun BudgetCard(
    remainingBudget: Long,
    progress: Float,
    onDetailClick: () -> Unit = {}
) {

    Column {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sisa Anggaran",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { onDetailClick() }) {
                Text(
                    text = "Lihat Detail",
                    color = PrimaryCyan
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp)
        ) {

            Column(
                modifier = Modifier
                    .background(LightCyan)
                    .padding(16.dp)
            ) {
                // Periode (optional dari UI kamu: "Oktober 2023")
                Text(
                    text = "Oktober 2023",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Remaining budget
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = PrimaryCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = MaterialTheme.typography.headlineSmall.fontSize
                            )
                        ) {
                            append("Rp ${remainingBudget.formatCurrency()} ")
                        }
                        withStyle(
                            SpanStyle(
                                color = Color.Gray,
                                fontWeight = FontWeight.Normal,
                                fontSize = MaterialTheme.typography.bodySmall.fontSize
                            )
                        ) {
                            append("tersisa")
                        }
                    }
                )
                Box(
                    modifier = Modifier
                        .background(
                            color = PrimaryCyan.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(50.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .align(Alignment.End),
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}% Terpakai",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryCyan,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress bar
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50)),
                    color = Color(0xFF00C2FF),
                    trackColor = Color(0xFFE6F7FF)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tip text
                Text(
                    text = "Tip: Pengeluaran makan Anda naik 15% minggu ini.",
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BudgetCardPreview() {
    BudgetCard(
        20,
        13f,
    )

}