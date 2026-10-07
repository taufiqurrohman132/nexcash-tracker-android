package com.example.nexcashapplication.feature.dashboard.presentation.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.extension.toFormatCurrencyVisibility
import com.example.nexcashapplication.ui.theme.PrimaryCyan


@Composable
fun BalanceCard(
    totalBalance: Long,
    income: Long,
    expense: Long,
    isVisible: Boolean,
    onEyeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isTotalSaldoOverflow by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = PrimaryCyan.copy(alpha = 0.35f),
                    spotColor = PrimaryCyan.copy(alpha = 0.35f)
                ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 0.dp   // dimatiin, shadow udah dihandle Modifier.shadow()
        ),
        shape = RoundedCornerShape(24.dp),
    ) {

        Box(
            modifier = Modifier
                    .fillMaxWidth(),
//                .background(
//                    brush = Brush.linearGradient(
//                        colors = listOf(
//                            PrimaryCyan,
//                            PrimaryCyan,
//                            SecondaryDeepPurple
//                        ),
//                        start = Offset(0f, 0f),
//                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
//                    )

//                )
        ) {
            Image(
                painter = painterResource(R.drawable.bg_balance_card),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                        .matchParentSize(),
            )

            Column(
                modifier = Modifier.padding(24.dp),
            ) {

                Text(
                    text = stringResource(R.string.dash_total_balance_label),
                    color = Color.White,
                )

                Spacer(
                    modifier = Modifier.height(8.dp),
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // helper sudah membawa prefix mata uang dan menyamarkan saldo tersembunyi
                    Text(
                        modifier = Modifier
                                .weight(1f),
                        text = totalBalance.toFormatCurrencyVisibility(isVisible, isTotalSaldoOverflow),
                        style =
                            MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        onTextLayout = { result ->
                            if (result.hasVisualOverflow) {
                                isTotalSaldoOverflow = result.hasVisualOverflow
                            }
                        },
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Box(
                        modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = Color.White.copy(0.2f),
                                    shape = CircleShape,
                                )
                                .clip(CircleShape)
                                .clickable { onEyeClick() },
                        contentAlignment = Alignment.Center,

                        ) {
                        Icon(
                            painter = painterResource(R.drawable.eye_show),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(20.dp),
                )

                Row {

                    SummaryChip(
                        title = stringResource(R.string.dash_income_label),
                        value = income,
                        modifier = Modifier.weight(1f),
                        icon = painterResource(R.drawable.down),
                        color = Color.Green,
                    )

                    Spacer(
                        modifier = Modifier.width(12.dp),
                    )

                    SummaryChip(
                        title = stringResource(R.string.dash_expense_label),
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
        {},
    )
}