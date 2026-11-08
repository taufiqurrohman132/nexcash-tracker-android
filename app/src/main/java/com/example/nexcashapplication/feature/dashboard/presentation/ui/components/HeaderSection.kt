package com.example.nexcashapplication.feature.dashboard.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.nexcashapplication.R

@Composable
fun HeaderSection(
    userName: String,
    onClickNotification: () -> Unit,
//    onClickProfile: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.dash_greeting_format, userName),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Row {
            IconButton(
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                ),
                onClick = onClickNotification
            ) {
                Icon(
                    Icons.Outlined.Notifications,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }

//            Spacer(
//                modifier = Modifier.width(8.dp)
//            )
//            AsyncImage(
//                model = "",
//                contentDescription = null,
//                modifier = Modifier
//                    .size(40.dp)
//                    .clip(CircleShape)
//                    .clickable{ onClickProfile()}
//            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    com.example.nexcashapplication.ui.theme.NexCashApplicationTheme {
        HeaderSection("kdf", {})
    }
}
