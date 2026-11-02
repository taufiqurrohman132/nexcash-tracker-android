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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.core.component.IconCircular


@Composable
fun LanguageItem(
    iconText: String,
    title: String,
    subtitle: String? = null,
    isSelectable: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val outlineColor = if (isSelectable) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    val iconBgColor = if (isSelectable) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val iconContentColor = if (isSelectable) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    ListItem(
        modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = if (isSelectable) 2.dp else 1.dp,
                    color = outlineColor,
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
                backgroundColor = iconBgColor,
                iconColor = iconContentColor,
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        trailingContent = {
            if (isSelectable) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        },
    )
}
