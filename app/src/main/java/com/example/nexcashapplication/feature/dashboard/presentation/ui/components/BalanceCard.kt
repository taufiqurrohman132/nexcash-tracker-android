package com.example.nexcashapplication.feature.dashboard.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.extension.formatCompact
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import com.example.nexcashapplication.ui.theme.SecondaryDeepPurple


@Composable
fun BalanceCard(
    totalBalance: Long,
    income: Long,
    expense: Long,
    isVisible: Boolean,
    onEyeClick: () -> Unit
) {
    var isTotalSaldoOverflow by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            PrimaryCyan,
                            PrimaryCyan,
                            SecondaryDeepPurple
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {

                Text(
                    text = "Total Saldo Anda",
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = if (!isVisible) {
                            "Rp ••••••••"
                        } else if (isTotalSaldoOverflow) {
                            "Rp ${totalBalance.formatCompact()}"
                        } else {
                            "Rp ${totalBalance.formatCurrency()}"
                        },
                        style =
                            MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        onTextLayout = { result ->
                            if (result.hasVisualOverflow) {
                                isTotalSaldoOverflow = result.hasVisualOverflow
                            }
                        }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color.White.copy(0.2f),
                                shape = CircleShape
                            )
                            .clip(CircleShape)
                            .clickable { onEyeClick() },
                        contentAlignment = Alignment.Center,

                    ) {
                        Icon(
                            painter = painterResource(R.drawable.eye_show),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Row {

                    SummaryChip(
                        title = "PEMASUKAN",
                        value = income,
                        modifier = Modifier.weight(1f),
                        icon = painterResource(R.drawable.down),
                        color = Color.Green,
                    )

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    SummaryChip(
                        title = "PENGELUARAN",
                        value = expense,
                        modifier = Modifier.weight(1f),
                        icon = painterResource(R.drawable.up),
                        color = Color.Red,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun BalanceCardPreview(modifier: Modifier = Modifier) {
    BalanceCard(
        4000,
        4040,
        5050,
        true,
        {}
    )
}