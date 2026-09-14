package com.example.nexcashapplication.feature.settings.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.core.component.IconCircular
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import com.example.nexcashapplication.ui.theme.TextSecondary
import com.example.nexcashapplication.ui.theme.TextTertiary


@Composable
fun LanguageItem(
    iconText: String,
    title: String,
    subtitle: String? = null,
    isSelectable: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = if (isSelectable) 2.dp else 1.dp,
                    color = if (isSelectable) {
                        PrimaryCyan
                    } else {
                        BorderGray
                    },
                    shape = RoundedCornerShape(16.dp),
                )
                .clickable(
                    onClick = {
                        onClick()
                    },
                )
                .padding(vertical = 4.dp),
        leadingContent = {
            IconCircular(
                iconText = iconText,
                backgroundColor = if (isSelectable) {
                    PrimaryCyan
                } else {
                    BorderGray
                },
                iconColor = if (isSelectable) {
                    Color.White
                } else {
                    TextSecondary
                },
            )
        },
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
        },
        supportingContent = {
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextTertiary,
                )
            }
        },
        trailingContent = {
            if (isSelectable) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PrimaryCyan,
                )
            }
        },
    )
}
