package com.example.nexcashapplication.feature.account.presentation.addedit.typeoption

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.account.presentation.addedit.typeoption.model.TypeAccountItem
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddEditTypeAccountScreen(
    viewModel: AddEditTypeAccountViewModel = hiltViewModel(),
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                AddEditTypeAccountEffect.NavigateBack -> navController.popBackStack()
                is AddEditTypeAccountEffect.NavigateToAddEditInfoAccount -> {
                    navController.navigate(
                        Route.AccountRoute.AddEditInfo(typeAccount = effect.typeAccount),
                    )
                }

                is AddEditTypeAccountEffect.ShowError -> TODO()
            }
        }
    }

    AddEditTypeAccountContent(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTypeAccountContent(
    state: AddEditTypeAccountState,
    onEvent: (AddEditTypeAccountEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Akun & Aset", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(AddEditTypeAccountEvent.OnBackClick) }) {
                        Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "Kembali")
                    }
                },
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
            ) {
                Button(
                    onClick = { onEvent(AddEditTypeAccountEvent.OnNextClick) },
                    enabled = state.isNextEnabled,
                    modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text("Lanjutkan")
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Langkah 1 dari 2: Identifikasi Aset",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }
        },
    ) { padding ->
        when {


            else -> {
                Column(
                    modifier = Modifier
                            .padding(padding)
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState()),
                ) {
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "Pilih Tipe Aset",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(16.dp))

                    state.listTypeAccounts.chunked(2).forEach { rowTypes ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            rowTypes.forEach { type ->
                                AssetTypeCard(
                                    type = type,
                                    selected = state.typeAccountSelected == type.type,
                                    onClick = {
                                        onEvent(
                                            AddEditTypeAccountEvent.OnTypeAccountSelected(
                                                type.type,
                                            ),
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            if (rowTypes.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    Spacer(Modifier.height(8.dp))
                    HintBox()
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}


@Composable
private fun HintBox() {
    Row(
        modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEAF6FF))
                .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.AutoMirrored.Outlined.TrendingUp,
            contentDescription = null,
            tint = Color(0xFF3B9FE0),
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "Anda dapat menambahkan beberapa aset dari tipe yang sama di langkah berikutnya.",
            style = MaterialTheme.typography.bodySmall,
            fontStyle = FontStyle.Italic,
            color = Color(0xFF2B7EBF),
        )
    }
}

@Composable
fun AssetTypeCard(
    type: TypeAccountItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Card(
        onClick = onClick,
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .background(type.iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    type.icon,
                    contentDescription = null,
                    tint = type.iconColor,
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = type.type.name,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium,
            )
            if (type.listProviders.isNotEmpty()) {
                Text(
                    text = type.type.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}


// ---------------------------------------------------------------------------
// PREVIEWS
// Asumsi: AccountAssetState & AccountUiModel punya field ini — sesuaikan
// nama field kalau berbeda dari data class asli kamu (tidak ada di file
// yang kamu paste, jadi ini tebakan dari cara field itu dipakai).
// ---------------------------------------------------------------------------

@Preview(showBackground = true, name = "Empty - No Selection")
@Composable
private fun AddAssetTypeContentPreview_Empty() {
    NexCashApplicationTheme() {
        AddEditTypeAccountContent(
            state = AddEditTypeAccountState(typeAccountSelected = null),
            onEvent = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}

@Preview(showBackground = true, name = "Selected - Bank")
@Composable
private fun AddAssetTypeContentPreview_Selected() {
    NexCashApplicationTheme {
        AddEditTypeAccountContent(
            state = AddEditTypeAccountState(typeAccountSelected = TypeAccount.BANK),
            onEvent = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}
