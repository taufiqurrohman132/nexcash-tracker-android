package com.example.nexcashapplication.feature.dashboard.presentation.ui.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun QuickActionSection() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
        Arrangement.SpaceEvenly
    ) {

        QuickActionItem(
            icon = Icons.Default.LocationOn,
            label = "Transfer"
        )

        QuickActionItem(
            icon = Icons.Default.Favorite,
            label = "Tagihan"
        )

        QuickActionItem(
            icon = Icons.Default.Call,
            label = "Scan"
        )

        QuickActionItem(
            icon = Icons.Default.MoreVert,
            label = "Lainnya"
        )
    }
}

@Composable
fun QuickActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit = {}
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {

        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            tonalElevation = 2.dp
        ) {

            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color(0xFF00C2FF)
                )
            }
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