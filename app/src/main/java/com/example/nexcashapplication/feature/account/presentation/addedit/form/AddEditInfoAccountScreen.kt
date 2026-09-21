package com.example.nexcashapplication.feature.account.presentation.addedit.form

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.nexcashapplication.core.component.FormField
import com.example.nexcashapplication.core.component.IconRounded
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.account.presentation.addedit.form.catalog.ProviderCatalog
import com.example.nexcashapplication.feature.account.presentation.addedit.form.model.AccountProviderUiModel
import com.example.nexcashapplication.feature.account.presentation.main.mapper.labelResId
import com.example.nexcashapplication.feature.transaction.presentation.addnew.AmountOutlinedTextField
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import com.example.nexcashapplication.ui.theme.TextSecondary
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddEditInfoAccountScreen(
    viewModel: AddEditInfoAccountViewModel = hiltViewModel(),
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                AddEditInfoAccountEffect.AssetSavedNavigateMain -> {
                    navController.navigate(Route.AccountRoute.Main) {
                        popUpTo(Route.AccountRoute.Main) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }

                AddEditInfoAccountEffect.NavigateBack -> navController.popBackStack()
                AddEditInfoAccountEffect.NavigateToProviderList -> TODO()
                is AddEditInfoAccountEffect.ShowError -> TODO()
            }
        }
    }

    AddEditAccountContent(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAccountContent(
    state: AddEditInfoAccountState,
    onEvent: (AddEditInfoAccountEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tambah Account",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(AddEditInfoAccountEvent.BackClicked) }) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
        bottomBar = {
            SimpanAsetButton(
                enabled = state.isSaveEnabled,
                isLoading = state.isSaving,
                isEditMode = state.isEditMode,
                onClick = { onEvent(AddEditInfoAccountEvent.SimpanClicked) },
            )
        },
    ) { padding ->
        when {
            else -> {
                Column(
                    modifier = Modifier
                            .padding(padding)
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    LangkahTerakhirBanner()
                    FormFieldEdit(
                        asetName = state.namaAset,
                        saldoAwal = state.saldoAwal.toString(),
                        typeAccountName = stringResource(state.typeAccount.labelResId),
                        onAsetNameChange = {
                            onEvent(AddEditInfoAccountEvent.NamaAsetChanged(it))
                        },
                        onSaldoAwalChange = {
                            onEvent(AddEditInfoAccountEvent.SaldoAwalChanged(it))
                        },
                        error = state.namaAsetError,
                    )

                    FormField(
                        title = "Pilih Provider (Opsional)",
                        icon = Icons.Outlined.AccountBalanceWallet,
                    ) {
                        if (state.providers.isEmpty()) {
                            Text(
                                "Tidak tersedia",
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                textAlign = TextAlign.Center,
                                color = TextSecondary,
                                style = MaterialTheme.typography.titleSmall,
                                fontStyle = FontStyle.Italic,
                            )
                        } else {
                            ProviderGrid(
                                providers = state.providers,
                                selectedProviderId = state.selectedProviderId,
                                onProviderSelected = {
                                    onEvent(AddEditInfoAccountEvent.ProviderSelected(it))
                                },
                            )
                        }
                    }

                }

            }
        }
    }
}

@Composable
fun ProviderGrid(
    modifier: Modifier = Modifier,
    providers: List<AccountProviderUiModel>,
    selectedProviderId: String?,
    onProviderSelected: (String) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        providers.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowItems.forEach { provider ->
                    ProviderGridItem(
                        name = provider.name,
                        isSelected = provider.id == selectedProviderId,
                        onSelect = { onProviderSelected(provider.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SimpanAsetButton(
    isEditMode: Boolean,
    enabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    Surface(color = Color.White, shadowElevation = 8.dp) {
        Button(
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryCyan,
                disabledContainerColor = PrimaryCyan.copy(alpha = 0.4f),
            ),
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(56.dp),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
            } else {
                AnimatedContent(
                    targetState = isEditMode,
                    label = "saveButtonText",
                ) { editMode ->
                    Text(
                        if (editMode) "Simpan Perubahan" else "Simpan Aset",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                    )
                }
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
            }
        }
    }
}


@Composable
fun FormFieldEdit(
    asetName: String,
    saldoAwal: String,
    typeAccountName: String,
    onAsetNameChange: (String) -> Unit,
    onSaldoAwalChange: (String) -> Unit,
    error: String? = null,
) {
    Column(
    ) {
        FormField(
            "Nama Aset (Alias)",
            icon = Icons.Outlined.AccountBalanceWallet,

            ) {
            OutlinedTextField(
                value = asetName,
                onValueChange = onAsetNameChange,
                placeholder = {
                    Text(
                        "Contoh: Tabungan Darurat",
                        color = TextSecondary.copy(alpha = 0.6f),
                    )
                },
                isError = error != null,
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryCyan,
                    unfocusedBorderColor = BorderGray,
                ),
                modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                error ?: "Gunakan nama yang mudah diingat",
                color = if (error != null) MaterialTheme.colorScheme.error else TextSecondary,
                fontSize = 12.sp,
            )
        }

        FormField(
            "Tipe Aset",
            icon = Icons.Outlined.AccountBalanceWallet,

            ) {
            OutlinedTextField(
                value = typeAccountName,
                onValueChange = {},
                isError = error != null,
                enabled = false,
                singleLine = true,
                trailingIcon = {
                    Surface(
                        shape = CircleShape,
                        color = BorderGray.copy(alpha = 0.5f),
                        modifier = Modifier.padding(end = 16.dp),
                    ) {
                        Text(
                            text = "TERKUNCI",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryCyan,
                    unfocusedBorderColor = BorderGray,
                ),
                modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
            )
        }

        FormField(
            "Saldo Awal",
            icon = Icons.Outlined.AccountBalanceWallet,
        ) {
            AmountOutlinedTextField(
                modifier = Modifier.height(50.dp),
                rawAmount = saldoAwal,
                onAmountChange = { newValue ->
                    onSaldoAwalChange(newValue)
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                prefix = {
                    Text("Rp ")
                },
            )
            Spacer(Modifier.height(6.dp))
            Text("Saldo saat ini dalam aset tersebut", color = TextSecondary, fontSize = 12.sp)

        }
    }
}

@Composable
fun ProviderGridItem(
    name: String,
    onSelect: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 2.dp,
                    color = if (isSelected) PrimaryCyan else BorderGray,
                    shape = RoundedCornerShape(16.dp),
                )
                .clickable { onSelect() }
                .padding(20.dp),
    ) {
        Text(
            name,
            color = if (isSelected) PrimaryCyan else BorderGray,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}


@Composable
private fun LangkahTerakhirBanner() {
    Row(
        modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(PrimaryCyan.copy(alpha = 0.1f))
                .padding(16.dp),
        verticalAlignment = Alignment.Top,
    ) {

        IconRounded(
            iconColor = PrimaryCyan,
            backgroundColor = PrimaryCyan.copy(alpha = 0.1f),
            imageVector = Icons.Outlined.Info,
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                "Langkah Terakhir",
                color = PrimaryCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "Lengkapi detail aset Anda untuk mulai mencatat transaksi secara otomatis.",
                color = TextSecondary,
                fontSize = 13.sp,
            )
        }
    }
}


// ---------------------------------------------------------------------------
// PREVIEWS
// Asumsi: AccountAssetState & AccountUiModel punya field ini — sesuaikan
// nama field kalau berbeda dari data class asli kamu (tidak ada di file
// yang kamu paste, jadi ini tebakan dari cara field itu dipakai).
// ---------------------------------------------------------------------------

@Preview(showBackground = true)
@Composable
private fun FormFieldEditPreview() {
    NexCashApplicationTheme {
//        FormFieldEdit()
    }
}

@Preview
@Composable
private fun AddEditAccountContentPreview() {
    NexCashApplicationTheme {
        AddEditAccountContent(
            state = AddEditInfoAccountState(
                providers = ProviderCatalog.getByType(TypeAccount.BANK),
            ),
            onEvent = { },
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}
