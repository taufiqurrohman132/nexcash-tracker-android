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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.component.FormField
import com.example.nexcashapplication.core.component.IconCircular
import com.example.nexcashapplication.core.domain.model.AppCurrency
import com.example.nexcashapplication.core.domain.model.AppLanguage
import com.example.nexcashapplication.core.i18n.resolve
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.settings.presentation.language.model.nameResId
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
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

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

                // Efek ini belum pernah dikirim, tapi handlernya diisi penuh (bukan
                // TODO() yang siap melempar NotImplementedError) dan Scaffold sudah
                // dipasangi SnackbarHost supaya siap dipakai kapan pun.
                is SettingsEffect.ShowSnackbar -> {
                    // resolve(context), bukan asString(): kita di dalam LaunchedEffect
                    // (bukan konteks komposisi), jadi composable tidak boleh dipanggil.
                    snackbarHostState.showSnackbar(effect.message.resolve(context))
                }
            }
        }
    }

    SettingsContent(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
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
                title = stringResource(R.string.settings_section_preferences),
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
                        title = stringResource(R.string.settings_language),
                        onClick = {
                            onEvent(SettingsEvent.OnMenuItemClick(SettingsDestinations.BAHASA))
                        },
                        content = {
                            Text(
                                text = stringResource(
                                    R.string.settings_language_format,
                                    stringResource(AppLanguage.fromCode(state.language).nameResId),
                                ),
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
                        title = stringResource(R.string.settings_currency),
                        onClick = {
                            onEvent(SettingsEvent.OnMenuItemClick(SettingsDestinations.MATA_UANG))
                        },
                        content = {
                            val currency = AppCurrency.fromCode(state.currency)
                            Text(
                                text = stringResource(
                                    R.string.settings_currency_format,
                                    stringResource(currency.displayNameRes),
                                    currency.code,
                                ),
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
                        title = stringResource(R.string.settings_theme_color),
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
                        title = stringResource(R.string.settings_notifications),
                        subtitle = stringResource(R.string.settings_notification_summary),
                        onClick = {
                            onEvent(SettingsEvent.OnMenuItemClick(SettingsDestinations.NOTIFIKASI))
                        },
                    )
                }
            }

            FormField(
                title = stringResource(R.string.settings_section_manage_data),
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
                        title = stringResource(R.string.settings_category),
                        onClick = {
                            onEvent(SettingsEvent.OnMenuItemClick(SettingsDestinations.KATEGORI))
                        },

                        )
                }
            }

            Text(
                text = stringResource(R.string.settings_app_version_format, state.appVersion),
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