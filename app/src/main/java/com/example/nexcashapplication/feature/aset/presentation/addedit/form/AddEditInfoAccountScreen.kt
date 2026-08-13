package com.example.nexcashapplication.feature.aset.presentation.addedit.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.nexcashapplication.core.component.FormField
import com.example.nexcashapplication.core.component.IconRounded
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
                AddEditInfoAccountEffect.AssetSavedNavigateHome -> TODO()
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
                onClick = { onEvent(AddEditInfoAccountEvent.SimpanClicked) },
            )
        },
    ) { padding ->
        when {
            else -> {
                Column(
                    modifier = Modifier
                            .padding(padding)
                            .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    LangkahTerakhirBanner()
                    FormFieldEdit(
                        asetName = state.namaAset,
                        saldoAwal = state.saldoAwal,
                        typeAccountName = state.typeAccount,
                        onAsetNameChange = {
                            onEvent(AddEditInfoAccountEvent.NamaAsetChanged(it))
                        },
                        onSaldoAwalChange = {
                            onEvent(AddEditInfoAccountEvent.SaldoAwalChanged(it))
                        },
                        error = state.namaAsetError,
                    )
                }

            }
        }
    }
}


@Composable
private fun SimpanAsetButton(
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
                Text(
                    "Simpan Aset",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
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
        verticalArrangement = Arrangement.spacedBy(8.dp),
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
                        .padding(top = 8.dp)
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
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryCyan,
                    unfocusedBorderColor = BorderGray,
                ),
                modifier = Modifier
                        .padding(top = 8.dp)
                        .height(50.dp)
                        .fillMaxWidth(),
            )
        }

        FormField(
            "Saldo Awal",
            icon = Icons.Outlined.AccountBalanceWallet,
        ) {
            OutlinedTextField(
                value = saldoAwal,
                onValueChange = onSaldoAwalChange,
                leadingIcon = { Text("Rp", color = TextSecondary, fontSize = 15.sp) },
                placeholder = { Text("0", color = TextSecondary.copy(alpha = 0.6f)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryCyan,
                    unfocusedBorderColor = BorderGray,
                ),
                modifier = Modifier
                        .padding(top = 8.dp)
                        .height(50.dp)
                        .fillMaxWidth(),
            )
            Spacer(Modifier.height(6.dp))
            Text("Saldo saat ini dalam aset tersebut", color = TextSecondary, fontSize = 12.sp)

        }
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
            state = AddEditInfoAccountState(),
            onEvent = { },
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}
