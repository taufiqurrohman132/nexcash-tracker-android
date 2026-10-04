package com.example.nexcashapplication.feature.notification.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nexcashapplication.feature.notification.presentation.main.model.AlertSeverity
import com.example.nexcashapplication.feature.notification.presentation.main.model.NotificationGroupUi
import com.example.nexcashapplication.feature.notification.presentation.main.model.NotificationUiModel

@Composable
fun NotificationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToBudget: (String) -> Unit,
    viewModel: NotificationViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val currentBack by rememberUpdatedState(onNavigateBack)
    val currentSettings by rememberUpdatedState(onNavigateToSettings)
    val currentBudget by rememberUpdatedState(onNavigateToBudget)

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                NotificationEffect.NavigateBack -> currentBack()
                NotificationEffect.NavigateToSettings -> currentSettings()
                is NotificationEffect.NavigateToBudget -> currentBudget(effect.budgetId)
            }
        }
    }

    NotificationContent(state = state, onEvent = viewModel::onEvent)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationContent(
    state: NotificationState,
    onEvent: (NotificationEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Notifikasi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(NotificationEvent.BackClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { onEvent(NotificationEvent.SettingsClicked) }) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Pengaturan notifikasi")
                    }
                },
            )
        },
    ) { padding ->
        when {
            state.isLoading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }

            state.isEmpty -> NotificationEmptyState(Modifier.padding(padding))

            else -> NotificationList(
                state = state,
                onEvent = onEvent,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun NotificationList(
    state: NotificationState,
    onEvent: (NotificationEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(key = "toolbar") {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (state.unreadCount > 0) "${state.unreadCount} belum dibaca"
                    else "Semua sudah dibaca",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(
                    onClick = { onEvent(NotificationEvent.MarkAllReadClicked) },
                    enabled = state.unreadCount > 0,
                ) { Text("Tandai semua dibaca") }
            }
        }
        state.groups.forEach { group ->
            item(key = "header-${group.header}") {
                Text(
                    text = group.header.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                )
            }
            items(group.items, key = { it.id }) { item ->
                NotificationItem(
                    item = item,
                    onClick = { onEvent(NotificationEvent.ItemClicked(item.id, item.budgetId)) },
                )
            }
        }
    }
}

@Composable
private fun NotificationItem(item: NotificationUiModel, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            SeverityIcon(item.severity)
            Column(Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (item.isRead) FontWeight.Medium else FontWeight.Bold,
                )
                Text(
                    text = item.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = item.timeLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (!item.isRead) {
                Box(
                    Modifier
                            .padding(top = 6.dp)
                            .size(8.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .semantics { contentDescription = "Belum dibaca" },
                )
            }
        }
    }
}

@Composable
private fun SeverityIcon(severity: AlertSeverity) {
    val cs = MaterialTheme.colorScheme
    val (container, content, icon) = when (severity) {
        AlertSeverity.Warning -> Triple(
            cs.tertiaryContainer,
            cs.onTertiaryContainer,
            Icons.Outlined.WarningAmber,
        )

        AlertSeverity.Limit -> Triple(
            cs.errorContainer,
            cs.onErrorContainer,
            Icons.Outlined.ErrorOutline,
        )
    }
    Box(
        Modifier.size(40.dp).background(container, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = content, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun NotificationEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Outlined.NotificationsNone, contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(12.dp))
        Text("Belum ada notifikasi", style = MaterialTheme.typography.titleMedium)
        Text(
            "Pengingat akan muncul di sini saat pengeluaran mencapai batas anggaran.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationContentPreview() {
    MaterialTheme { // ganti dengan NexCashTheme
        NotificationContent(
            state = NotificationState(
                isLoading = false,
                unreadCount = 1,
                groups = listOf(
                    NotificationGroupUi(
                        "Hari ini",
                        listOf(
                            NotificationUiModel(
                                "1", "1", "Belanja Bulanan mencapai 100%",
                                "Batas anggaran Oktober sudah tercapai.", "1 jam lalu",
                                AlertSeverity.Limit, isRead = false,
                            ),
                        ),
                    ),
                    NotificationGroupUi(
                        "Kemarin",
                        listOf(
                            NotificationUiModel(
                                "2", "2", "Transportasi mencapai 80%",
                                "Anggaran Oktober hampir habis.", "Kemarin, 18.40",
                                AlertSeverity.Warning, isRead = true,
                            ),
                        ),
                    ),
                ),
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationEmptyPreview() {
    MaterialTheme { NotificationContent(NotificationState(isLoading = false), onEvent = {}) }
}