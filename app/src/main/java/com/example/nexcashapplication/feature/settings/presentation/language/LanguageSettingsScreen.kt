package com.example.nexcashapplication.feature.settings.presentation.language

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.nexcashapplication.core.component.AppButton
import com.example.nexcashapplication.core.component.AppSearchBar
import com.example.nexcashapplication.core.component.IconCircular
import com.example.nexcashapplication.feature.settings.presentation.component.LanguageItem
import com.example.nexcashapplication.feature.settings.presentation.language.model.LanguageItemUiModel
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations.BAHASA
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations.MATA_UANG
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations.NOTIFIKASI
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations.PUSAT_BANTUAN
import com.example.nexcashapplication.feature.settings.presentation.main.model.SettingsDestinations.TEMA_WARNA
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import com.example.nexcashapplication.ui.theme.TextSecondary
import com.example.nexcashapplication.ui.theme.TextTertiary

@Composable
fun LanguageSettingsScreen(
    viewModel: LanguageSettingsViewModel = hiltViewModel(),
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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
                    }
                }

                is LanguageSettingsEffect.ShowSnackbar -> TODO()
                LanguageSettingsEffect.NavigateBack -> navController.popBackStack()
            }
        }
    }

    LanguageSettingsContent(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSettingsContent(
    state: LanguageSettingsState,
    onEvent: (LanguageSettingsEvent) -> Unit,
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
                navigationIcon = {
                    IconButton(onClick = { onEvent(LanguageSettingsEvent.OnBackClick) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        bottomBar = {
            AppButton(
                title = "Simpan",
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
                placeholder = "Cari Bahasa",
                onSearchClear = {
                    onEvent(LanguageSettingsEvent.OnSearchClear)
                },
            )


            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ){
                items(
                    state.listLanguage,
                    key = { it.id },
                ) {
                    LanguageItem(
                        iconText = it.id.uppercase(),
                        title = it.name,
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
                    BorderGray,
                    RoundedCornerShape(16.dp),
                )
                .background(Color.White)
                .padding(16.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = PrimaryCyan,
        )

        Text(
            text = "Bahasa yang Anda pilih akan diterapkan ke seluruh antarmuka aplikasi KeuanganKu untuk memudahkan pengelolaan finansial Anda.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
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
                        name = "Indonesia",
                    ),
                    LanguageItemUiModel(
                        id = "en",
                        name = "English",
                    ),
                ),
            ),
            onEvent = {},
        )
    }
}