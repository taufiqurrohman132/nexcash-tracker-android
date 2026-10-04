package com.example.nexcashapplication.feature.settings.presentation.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.settings.presentation.main.component.ThemeColorSheet
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations.BAHASA
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations.MATA_UANG
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
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingsEffect.NavigateTo -> {
                    Log.d(TAG, "SettingsScreen: effect ${effect.destination}")
                    when (effect.destination) {
                        // Tema & Warna tidak pindah layar: sheet-nya dibuka dan
                        // ditutup lewat state.showThemeColorSheet di ViewModel.
                        TEMA_WARNA -> {
                        }

                        NOTIFIKASI -> {
                            // layar edit notifikasi MVI; SettingsRoute.Notifikasi
                            // body-nya masih kosong di NavigationStack
                            navController.navigate(
                                Route.NotificationRoute.Edit,
                            )
                        }

                        PUSAT_BANTUAN -> {

                        }

                        BAHASA -> {
                            navController.navigate(
                                Route.SettingsRoute.Bahasa,
                            )
                        }

                        MATA_UANG -> {
                            navController.navigate(
                                Route.SettingsRoute.MataUang,
                            )
                        }

                        SettingsDestinations.KATEGORI -> {
                            navController.navigate(
                                Route.CategoryRoute.Main
                            )
                        }
                    }
                }

                // Scaffold ini belum punya SnackbarHost — TODO() di sini akan melempar
                // NotImplementedError dan men crash aplikasi kalau efeknya pernah dikirim.
                is SettingsEffect.ShowSnackbar -> {
                }
            }
        }
    }

    SettingsContent(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
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
        val sheetState = rememberModalBottomSheetState()

        Column(
            modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp),
        ) {
            FormField(
                title = "Preferensi",
                textColor = TextTertiary,
            ) {
                Column(
                    modifier = Modifier.border(
                        1.dp,
                        BorderGray,
                        RoundedCornerShape(16.dp),
                    )
                ){

                    SettingsItem(
                        icon = Icons.Outlined.Language,
                        title = "Bahasa",
                        onClick = {
                            onEvent(SettingsEvent.OnMenuItemClick(SettingsDestinations.BAHASA))
                        },
                        content = {
                            Text(
                                text = state.language,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextTertiary,
                            )
                        },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = BorderGray,
                    )
                    SettingsItem(
                        icon = Icons.Outlined.CurrencyExchange,
                        title = "Mata Uang",
                        onClick = {
                            onEvent(SettingsEvent.OnMenuItemClick(SettingsDestinations.MATA_UANG))
                        },
                        content = {
                            Text(
                                text = state.currency,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextTertiary,
                            )
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
                        content = {
                            Box(
                                modifier = Modifier
                                        .width(36.dp)
                                        .height(18.dp)
                                        .background(
                                            state.color,
                                            shape = RoundedCornerShape(8.dp),
                                        ),
                            )
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
                            onEvent(SettingsEvent.OnMenuItemClick(SettingsDestinations.NOTIFIKASI))
                        },
                    )
                }
            }

            FormField(
                title = "Kelola Data",
                textColor = TextTertiary,

                ) {
                Column(
                    modifier = Modifier.border(
                        1.dp,
                        BorderGray,
                        RoundedCornerShape(16.dp),
                    )
                ){
                    SettingsItem(
                        icon = Icons.Outlined.Category,
                        title = "Kategori",
                        onClick = {
                            onEvent(SettingsEvent.OnMenuItemClick(SettingsDestinations.KATEGORI))
                        },

                        )
                }
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



        if (state.showThemeColorSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    onEvent(SettingsEvent.OnThemeSheetDismiss)
                },
                sheetState = sheetState,
            ) {
                ThemeColorSheet(
                    onColorClick = { color ->
                        onEvent(SettingsEvent.OnColorSelected(color))
                    },
                    onThemeClick = {

                    },
                    initialThemeSelected = "Default",
                    intitalColorSelected = state.color,
                )
            }
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
                    onClick = onClick,
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
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
            state = SettingsState(

            ),
            onEvent = {},
        )
    }
}

private val TAG = "SettingsScreen"