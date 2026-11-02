package com.example.nexcashapplication.feature.settings.presentation.language

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.LocaleList
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.component.AppButton
import com.example.nexcashapplication.core.component.AppSearchBar
import com.example.nexcashapplication.core.i18n.resolve
import com.example.nexcashapplication.feature.settings.presentation.component.LanguageItem
import com.example.nexcashapplication.feature.settings.presentation.language.model.LanguageItemUiModel
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations.BAHASA
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations.KATEGORI
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations.MATA_UANG
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations.NOTIFIKASI
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations.PUSAT_BANTUAN
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations.TEMA_WARNA
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import java.util.Locale

@Composable
fun LanguageSettingsScreen(
    viewModel: LanguageSettingsViewModel = hiltViewModel(),
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LanguageSettingsEffect.NavigateTo -> {
                    when (effect.destination) {
                        TEMA_WARNA -> TODO()
                        NOTIFIKASI -> TODO()
                        PUSAT_BANTUAN -> TODO()
                        BAHASA -> TODO()
                        MATA_UANG -> TODO()
                        KATEGORI -> TODO()
                    }
                }

                is LanguageSettingsEffect.ShowSnackbar -> {
                    // resolve(context), bukan asString(): kita di dalam LaunchedEffect
                    // (bukan konteks komposisi), jadi composable tidak boleh dipanggil.
                    snackbarHostState.showSnackbar(effect.message.resolve(context))
                }

                LanguageSettingsEffect.NavigateBack -> navController.popBackStack()

                // Bahasa sudah tersimpan & AppLocale diterapkan: tutup layar, lalu paksa
                // Activity dibuat ulang supaya resources (string resource) ikut berbahasa baru.
                is LanguageSettingsEffect.Saved -> {
                    navController.popBackStack()
                    applyLanguage(context, effect.languageCode)
                }
            }
        }
    }

    LanguageSettingsContent(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSettingsContent(
    state: LanguageSettingsState,
    onEvent: (LanguageSettingsEvent) -> Unit,
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
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                navigationIcon = {
                    IconButton(onClick = { onEvent(LanguageSettingsEvent.OnBackClick) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.settings_back),
                        )
                    }
                },
            )
        },
        bottomBar = {
            AppButton(
                title = stringResource(R.string.settings_save),
                onClick = { onEvent(LanguageSettingsEvent.OnSaveClick) },
                modifier = Modifier.padding(16.dp),
                isEnabled = state.hasChange,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AppSearchBar(
                searchQuery = state.searchQuery,
                onSearchQueryChange = {
                    onEvent(LanguageSettingsEvent.OnSearchQueryChange(it))
                },
                placeholder = stringResource(R.string.settings_search_language),
                onSearchClear = {
                    onEvent(LanguageSettingsEvent.OnSearchClear)
                },
            )


            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                items(
                    state.listLanguage,
                    key = { it.id },
                ) {
                    LanguageItem(
                        iconText = it.id.uppercase(),
                        title = stringResource(it.nameRes),
                        onClick = {
                            onEvent(LanguageSettingsEvent.OnItemClick(it.id))
                        },
                        isSelectable = it.id == state.selectedLanguageId,
                    )
                }

                item {
                    Spacer(Modifier.height(16.dp))
                    InformationCard()
                }
            }


        }
    }
}

@Composable
private fun InformationCard(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant,
                    RoundedCornerShape(16.dp),
                )
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )

        Text(
            text = stringResource(R.string.settings_language_info),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp),
        )

    }
}


@Preview
@Composable
private fun LanguageSettingsContentPreview() {
    NexCashApplicationTheme {
        LanguageSettingsContent(
            state = LanguageSettingsState(
                listLanguage = listOf(
                    LanguageItemUiModel(
                        id = "df",
                        nameRes = R.string.settings_lang_id,
                    ),
                    LanguageItemUiModel(
                        id = "en",
                        nameRes = R.string.settings_lang_en,
                    ),
                ),
            ),
            onEvent = {},
        )
    }
}


/**
 * Terapkan [languageCode] ke sistem lalu pastikan Activity dibuat ulang.
 *
 * - API 33+: dicatat lewat [LocaleManager.setApplicationLocales] sehingga ikut muncul di
 *   System Settings → App languages, dan sistem sendiri me-recreate Activity (konfigurasi
 *   berubah) — recreate manual tidak perlu.
 * - API < 33: tidak ada jalur sistem, jadi Activity di-recreate manual; bahasa tetap benar
 *   karena `AppLocale.apply` sudah mengganti Locale default dan `attachBaseContext` membungkus
 *   resources dari [com.example.nexcashapplication.core.i18n.AppLocale.locale].
 */
private fun applyLanguage(context: Context, languageCode: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val manager =
            runCatching { context.getSystemService(LocaleManager::class.java) }.getOrNull()
        if (manager != null) {
            val applied = runCatching {
                manager.applicationLocales = LocaleList(Locale.forLanguageTag(languageCode))
            }.isSuccess
            if (applied) return
        }
    }
    context.findActivity()?.recreate()
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}