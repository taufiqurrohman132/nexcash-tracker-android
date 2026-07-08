package com.example.nexcashapplication.feature.dashboard.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.R
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.LightCyan
import com.example.nexcashapplication.ui.theme.PrimaryCyan

@Composable
fun QuickActionSection() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceEvenly
    ) {

        QuickActionItem(
            icon = painterResource(R.drawable.transfer),
            label = "Transfer"
        )

        QuickActionItem(
            icon = painterResource(R.drawable.bill_dollar),
            label = "Tagihan"
        )

        QuickActionItem(
            icon = painterResource(R.drawable.scan),
            label = "Scan"
        )

        QuickActionItem(
            icon = painterResource(R.drawable.more_horizontal),
            label = "Lainnya"
        )
    }
}

@Composable
fun QuickActionItem(
    icon: Painter,
    label: String,
    onClick: () -> Unit = {}
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .shadow(
                    elevation = 4.dp,
                    ambientColor = PrimaryCyan,
                    spotColor = PrimaryCyan,
                    shape = CircleShape,
                    clip = false
                )
                .clip(CircleShape)
                .background(
                    color = LightCyan,
                    shape = CircleShape,
                )
                .border(
                    1.5.dp,
                    color = BorderGray,
                    shape = CircleShape
                )
                .clickable { onClick() },
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                painter = icon,
                contentDescription = label,
                tint = PrimaryCyan
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun QuickActionSectionPreview() {
    QuickActionSection()

}