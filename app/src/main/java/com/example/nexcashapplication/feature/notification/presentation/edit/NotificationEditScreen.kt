package com.example.nexcashapplication.feature.notification.presentation.edit

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NotificationEditScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationSettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentBack by rememberUpdatedState(onNavigateBack)

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        viewModel.onEvent(NotificationSettingsEvent.PermissionRequestResult(granted))
    }

    // Cek ulang tiap kembali dari layar pengaturan sistem
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onEvent(
            NotificationSettingsEvent.PermissionChecked(context.hasNotificationPermission()),
        )
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                NotificationSettingsEffect.NavigateBack -> currentBack()
                NotificationSettingsEffect.RequestNotificationPermission ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
            }
        }
    }

    NotificationSettingsContent(state = state, onEvent = viewModel::onEvent)
}

private fun Context.hasNotificationPermission(): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsContent(
    state: NotificationSettingsState,
    onEvent: (NotificationSettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan Notifikasi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(NotificationSettingsEvent.BackClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "master") {
                MasterToggleCard(
                    checked = state.remindersActive,
                    showPermissionHint = state.masterEnabled && !state.permissionGranted,
                    onToggle = { onEvent(NotificationSettingsEvent.MasterToggled(it)) },
                )
            }
            item(key = "label") {
                Text(
                    "ANGGARAN AKTIF",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                )
            }
            items(state.budgets, key = { it.budgetId }) { budget ->
                BudgetAlertCard(
                    item = budget,
                    controlsEnabled = state.remindersActive,
                    onToggle = {
                        onEvent(
                            NotificationSettingsEvent.BudgetToggled(
                                budget.budgetId,
                                it,
                            ),
                        )
                    },
                    onThreshold = { percent, selected ->
                        onEvent(
                            NotificationSettingsEvent.ThresholdToggled(
                                budget.budgetId,
                                percent,
                                selected,
                            ),
                        )
                    },
                )
            }
            item(key = "footnote") {
                Text(
                    "Pengingat muncul sebagai notifikasi di perangkat ini, satu kali per batas dalam setiap periode anggaran.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun MasterToggleCard(
    checked: Boolean,
    showPermissionHint: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CategoryIconBox(Icons.Outlined.Notifications)
                Column(Modifier.weight(1f)) {
                    Text(
                        "Aktifkan pengingat",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "Beri tahu saat pengeluaran mendekati batas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(checked = checked, onCheckedChange = onToggle)
            }
        }
        if (showPermissionHint) {
            Text(
                "Izin notifikasi belum diberikan. Aktifkan lewat pengaturan sistem.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
        }
    }
}

@Composable
private fun BudgetAlertCard(
    item: BudgetAlertUi,
    controlsEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    onThreshold: (percent: Int, selected: Boolean) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CategoryIconBox(categoryIcon(item.iconKey))
                Text(item.name, Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                Switch(
                    checked = item.enabled,
                    onCheckedChange = onToggle,
                    enabled = controlsEnabled,
                    modifier = Modifier.semantics { contentDescription = "Pengingat ${item.name}" },
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Ingatkan di",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                AlertThreshold.all.forEach { percent ->
                    val selected = percent in item.thresholds
                    FilterChip(
                        selected = selected,
                        onClick = { onThreshold(percent, !selected) },
                        enabled = controlsEnabled && item.enabled,
                        label = { Text("$percent%") },
                        // centang = state tidak hanya bergantung warna
                        leadingIcon = if (selected) {
                            {
                                Icon(
                                    Icons.Filled.Check,
                                    null,
                                    Modifier.size(FilterChipDefaults.IconSize),
                                )
                            }
                        } else null,
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryIconBox(icon: ImageVector) {
    Box(
        Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
    }
}

// Domain simpan String, UI yang map ke ImageVector. Pakai mapper icon yang sudah ada kalau ada.
internal fun categoryIcon(key: String): ImageVector = when (key) {
    "food" -> Icons.Outlined.Restaurant
    "transport" -> Icons.Outlined.DirectionsCar
    "shopping" -> Icons.Outlined.ShoppingBag
    else -> Icons.Outlined.Category
}

@Preview(showBackground = true)
@Composable
private fun NotificationSettingsPreview() {
    MaterialTheme { // ganti dengan NexCashTheme
        NotificationSettingsContent(
            state = NotificationSettingsState(
                isLoading = false, masterEnabled = true,
                budgets = listOf(
                    BudgetAlertUi("1", "Makanan & Minuman", "food", true, setOf(80, 100)),
                    BudgetAlertUi("2", "Transportasi", "transport", true, setOf(80)),
                    BudgetAlertUi("3", "Belanja Bulanan", "shopping", false, setOf(80)),
                ),
            ),
            onEvent = {},
        )
    }
}