package com.example.nexcashapplication.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.ui.theme.AccentTealGreen
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import com.example.nexcashapplication.ui.theme.TextSecondary

@Composable
fun TransactionItem(
    transaction: TransactionItemUiModel,
    onClick: (id: String) -> Unit,
) {

    ListItem(
        leadingContent = {
            Box(
                modifier = Modifier
                        .background(
                            color = PrimaryCyan.copy(alpha = 0.1f),
                            shape = CircleShape,
                        )
                        .size(50.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.insights),
                    tint = PrimaryCyan,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            }
        },
        headlineContent = {
            Text(
                text = transaction.name,
                fontWeight = FontWeight.Bold,
            )
        },
        supportingContent = {
            Text(
                text = "${transaction.account} - ${transaction.time}",
                color = TextSecondary,
            )
        },
        trailingContent = {
            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = if (transaction.isIncome)
                        "+ Rp ${transaction.amount.formatCurrency()}"
                    else
                        "- Rp ${transaction.amount.formatCurrency()}",
                    color = if (transaction.isIncome) AccentTealGreen else Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
                Box(
                    modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = BorderGray,
                                shape = RoundedCornerShape(50.dp),
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = transaction.category, // teks biasa di bawah
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun TransactionItemPreview() {
    TransactionItem(
        TransactionItemUiModel(
            id = "1",
            name = "Transfer Gaji",
            time = "23 okt, 10:45",
            amount = 5_000_000,
            isIncome = true,
            category = "Salary",
            account = "BCA Finance",
            icon = Icons.Default.Category,
        ),
        onClick = {},
    )
}