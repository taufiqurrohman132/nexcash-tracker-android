package com.example.nexcashapplication.feature.settings.presentation.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.nexcashapplication.core.component.FormField
import com.example.nexcashapplication.core.component.IconCircular
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations.BAHASA_MATA_UANG
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations.NOTIFIKASI
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations.PUSAT_BANTUAN
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations.TEMA_WARNA
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import com.example.nexcashapplication.ui.theme.TextTertiary

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingsEffect.NavigateTo -> {
                    when (effect.destination) {
                        BAHASA_MATA_UANG -> {

                        }

                        TEMA_WARNA -> TODO()
                        NOTIFIKASI -> TODO()
                        PUSAT_BANTUAN -> TODO()
                    }
                }

                is SettingsEffect.ShowSnackbar -> TODO()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp),
        ) {
            FormField(
                title = "Preferensi",
                textColor = TextTertiary,
            ) {
                SettingsItem(
                    icon = Icons.Outlined.Language,
                    title = "Bahasa",
                    onClick = {
                        onEvent(SettingsEvent.OnMenuItemClick(SettingsDestinations.BAHASA_MATA_UANG))
                    },
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = BorderGray,
                )
                SettingsItem(
                    icon = Icons.Outlined.Palette,
                    title = "Tema & Warna",
                    onClick = {
                        onEvent(SettingsEvent.OnMenuItemClick(SettingsDestinations.TEMA_WARNA))
                    },
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = BorderGray,
                )
                SettingsItem(
                    icon = Icons.Outlined.Notifications,
                    title = "Notification",
                    subtitle = "Saldo, Budget",
                    onClick = {

                    },
                )
            }
            Text(
                text = "Versi Aplikasi ${state.appVersion}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
            )

        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    content: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable(
                    onClick = {

                    },
                )
                .padding(vertical = 4.dp),
        leadingContent = {
            IconCircular(
                icon = icon,
                iconToCountainerRatio = 2,
                backgroundColor = BorderGray,
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
            Row {
                content?.invoke()

                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = BorderGray,
                )
            }
        },
    )
}


@Preview
@Composable
private fun SettingsContentPreview() {
    NexCashApplicationTheme {
        SettingsContent(
            state = SettingsState(),
            onEvent = {},
        )
    }
}