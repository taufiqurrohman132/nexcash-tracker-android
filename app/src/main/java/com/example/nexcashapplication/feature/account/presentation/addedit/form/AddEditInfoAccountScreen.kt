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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import com.example.nexcashapplication.core.component.AppSnackbarHost
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
import androidx.compose.ui.platform.LocalContext
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
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.component.FormField
import com.example.nexcashapplication.core.component.IconRounded
import com.example.nexcashapplication.core.currency.AppCurrencyHolder
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.core.i18n.asString
import com.example.nexcashapplication.core.i18n.resolve
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.account.presentation.addedit.form.catalog.ProviderCatalog
import com.example.nexcashapplication.feature.account.presentation.addedit.form.model.AccountProviderUiModel
import com.example.nexcashapplication.feature.account.presentation.main.mapper.labelResId
import com.example.nexcashapplication.feature.transaction.presentation.addnew.AmountOutlinedTextField
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddEditInfoAccountScreen(
    viewModel: AddEditInfoAccountViewModel = hiltViewModel(),
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

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
                is AddEditInfoAccountEffect.ShowError -> {
                    // resolve(context), bukan asString(): kita di dalam LaunchedEffect
                    // (bukan konteks komposisi), jadi composable tidak boleh dipanggil.
                    snackbarHostState.showSnackbar(effect.message.resolve(context))
                }
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
        snackbarHost = { AppSnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.acct_add_account),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(AddEditInfoAccountEvent.BackClicked) }) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = stringResource(R.string.acct_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
            )
        },
        bottomBar = {
            SimpanAsetButton(
                enabled = state.isSaveEnabled,
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
                        title = stringResource(R.string.acct_choose_provider),
                        icon = Icons.Outlined.AccountBalanceWallet,
                    ) {
                        if (state.providers.isEmpty()) {
                            Text(
                                stringResource(R.string.acct_provider_unavailable),
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
    onClick: () -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
        Button(
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            ),
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(56.dp),
        ) {
            AnimatedContent(
                targetState = isEditMode,
                label = "saveButtonText",
            ) { editMode ->
                Text(
                    stringResource(
                        if (editMode) R.string.acct_save_changes else R.string.acct_save_asset,
                    ),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
            }
            Spacer(Modifier.width(6.dp))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
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
    error: UiText? = null,
) {
    Column(
    ) {
        FormField(
            stringResource(R.string.acct_asset_name),
            icon = Icons.Outlined.AccountBalanceWallet,

            ) {
            OutlinedTextField(
                value = asetName,
                onValueChange = onAsetNameChange,
                placeholder = {
                    Text(
                        stringResource(R.string.acct_name_placeholder),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                isError = error != null,
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                ),
                modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                error?.asString() ?: stringResource(R.string.acct_name_hint),
                color = if (error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
        }

        FormField(
            stringResource(R.string.acct_asset_type),
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
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(end = 16.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.acct_locked),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                ),
                modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
            )
        }

        FormField(
            stringResource(R.string.acct_initial_balance),
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
                    Text(AppCurrencyHolder.value.prefix)
                },
            )
            Spacer(Modifier.height(6.dp))
            Text(
                stringResource(R.string.acct_balance_hint),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )

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
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(16.dp),
                )
                .clickable { onSelect() }
                .padding(20.dp),
    ) {
        Text(
            name,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
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
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .padding(16.dp),
        verticalAlignment = Alignment.Top,
    ) {

        IconRounded(
            iconColor = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            imageVector = Icons.Outlined.Info,
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                stringResource(R.string.acct_step_last),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                stringResource(R.string.acct_step_last_desc),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
