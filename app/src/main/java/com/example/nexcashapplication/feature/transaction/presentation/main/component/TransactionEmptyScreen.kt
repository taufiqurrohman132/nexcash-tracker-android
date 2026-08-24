package com.example.nexcashapplication.feature.transaction.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.core.component.IconRounded
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import com.example.nexcashapplication.ui.theme.PrimaryCyan

@Composable
fun TransactionEmptyScreen(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {

        Box {
            Box(
                modifier = Modifier
                        .padding(8.dp)
                        .size(200.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(PrimaryCyan.copy(alpha = 0.1f)),
            ) {

            }

            IconRounded(
                imageVector = Icons.Outlined.AccountBalanceWallet,
                iconColor = Color.White,
                backgroundColor = PrimaryCyan,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.align(Alignment.BottomEnd),
            )
        }

        Text(
            "Belum ada transaksi",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        Text(
            "Mulai catat pengeluaran dan pemasukanmu hari ini agar keuangan lebih terkontrol dan tujuan finansialmu tercapai.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )

        Button(
            onClick = { onAddClick() },
            modifier = Modifier
                    .padding(16.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = PrimaryCyan,
                        spotColor = PrimaryCyan,
                    )
                    .fillMaxWidth()
                    .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryCyan,
            ),
        ) {
            Text(
                "Tambah Transaksi Pertama",
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }
}

@Preview
@Composable
private fun TransactionEmptyScreenPreview() {
    NexCashApplicationTheme {
        TransactionEmptyScreen(
            onAddClick = {},
        )

    }
}