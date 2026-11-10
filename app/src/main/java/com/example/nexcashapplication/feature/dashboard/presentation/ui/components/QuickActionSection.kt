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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.extension.rememberSingleClick

@Composable
fun QuickActionSection(
    modifier: Modifier = Modifier,
    onKategoriClick: () -> Unit = {},
    onTambahAkunClick: () -> Unit = {},
    onStatistikClick: () -> Unit = {},
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceEvenly
    ) {

        // Hanya pintu yang benar-benar ada (semua route terdaftar di NavHost).
        // Transfer/Tagihan/Scan dibuang — fiturnya tidak ada, tombolnya jadi mati.
        QuickActionItem(
            icon = painterResource(R.drawable.category_list),
            label = stringResource(R.string.dash_quick_action_category),
            onClick = onKategoriClick,
        )

        QuickActionItem(
            icon = painterResource(R.drawable.account_balance_bank),
            label = stringResource(R.string.dash_quick_action_add_account),
            onClick = onTambahAkunClick,
        )

        QuickActionItem(
            icon = painterResource(R.drawable.insights),
            label = stringResource(R.string.dash_quick_action_statistics),
            onClick = onStatistikClick,
        )
    }
}

@Composable
fun QuickActionItem(
    icon: Painter,
    label: String,
    onClick: () -> Unit = {}
) {
    val singleClick = rememberSingleClick(onClick = onClick)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape,
                )
                .border(
                    1.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = CircleShape
                )
                .clickable { singleClick() },
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                painter = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun QuickActionSectionPreview() {
    com.example.nexcashapplication.ui.theme.NexCashApplicationTheme {
        QuickActionSection()
    }
}