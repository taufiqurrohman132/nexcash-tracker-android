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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.ui.theme.LightCyan
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

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
                text = stringResource(R.string.dash_remaining_budget),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { onDetailClick() }) {
                Text(
                    text = stringResource(R.string.dash_see_detail),
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
                // Periode bulan/tahun berjalan, terlokalisasi (bukan placeholder
                // hardcoded "Oktober 2023")
                val now = YearMonth.now()
                val period = stringResource(
                    R.string.dash_budget_period,
                    now.month.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault()),
                    now.year,
                )
                Text(
                    text = period,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Remaining budget — format nilai sekali, bukan tiap recomposition
                val remainingAmount = remember(remainingBudget) {
                    remainingBudget.formatCurrency()
                }
                val formattedRemaining = stringResource(
                    R.string.total_balance_format,
                    remainingAmount,
                )
                val remainingSuffix = stringResource(R.string.dash_remaining_suffix)
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = PrimaryCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = MaterialTheme.typography.headlineSmall.fontSize
                            )
                        ) {
                            append("$formattedRemaining ")
                        }
                        withStyle(
                            SpanStyle(
                                color = Color.Gray,
                                fontWeight = FontWeight.Normal,
                                fontSize = MaterialTheme.typography.bodySmall.fontSize
                            )
                        ) {
                            append(remainingSuffix)
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
                        text = stringResource(
                            R.string.dash_budget_used_format,
                            (progress * 100).toInt(),
                        ),
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
                    text = stringResource(R.string.dash_budget_tip),
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