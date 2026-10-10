package com.example.nexcashapplication.feature.account.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.component.FormField
import com.example.nexcashapplication.core.component.IconCircular
import com.example.nexcashapplication.core.currency.AppCurrencyHolder
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.core.extension.toFormatCurrencyVisibility
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.core.i18n.asString
import com.example.nexcashapplication.core.i18n.resolve
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.account.presentation.addedit.form.model.AccountProviderUiModel
import com.example.nexcashapplication.feature.account.presentation.addedit.typeoption.mapper.toUiItem
import com.example.nexcashapplication.feature.account.presentation.main.mapper.labelResId
import com.example.nexcashapplication.feature.account.presentation.main.model.AccountUiGroupModel
import com.example.nexcashapplication.feature.account.presentation.main.model.AccountUiModel
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import com.example.nexcashapplication.ui.theme.SecondaryDeepPurple
import com.example.nexcashapplication.ui.theme.TextTertiary
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AccountAssetScreen(
    viewModel: AccountAssetViewModel = hiltViewModel(),
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is AccountAssetEffect.NavigateToAccountDetail -> navController.navigate(
                    Route.AccountRoute.Detail(effect.accountId),
                )

                AccountAssetEffect.NavigateToAddAccount -> navController.navigate(
                    Route.AccountRoute.AddEditTypeAccount,
                )

                // Snackbar host sudah terpasang di Content — tidak perlu Toast
                is AccountAssetEffect.ShowError ->
                    snackbarHostState.showSnackbar(effect.message.resolve(context))
            }
        }
    }

    AccountAssetContent(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountAssetContent(
    state: AccountAssetState,
    onEvent: (AccountAssetEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.acct_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                },
                actions = {
                    IconButton(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onClick = { onEvent(AccountAssetEvent.OnAddAccountClick) },
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.acct_add_content_desc))
                    }
                },
            )
        },
    ) { padding ->
        when {
            state.isLoading && state.bankAccounts.isEmpty() && state.eWallets.isEmpty() -> {
                // eWallets tidak pernah diisi VM (semua grup, termasuk E-Wallet, masuk
                // `bankAccounts`) — kondisinya tetap dipertahankan agar aman bila kelak diisi.
                // Dulu cabang ini cuma Spacer kosong: layar putih sampai data pertama masuk.
                Box(
                    Modifier
                            .fillMaxSize()
                            .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null && state.bankAccounts.isEmpty() -> {
                Box(
                    Modifier
                            .fillMaxSize()
                            .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // `state.error` dipastikan non-null oleh kondisi cabang di atas
                        Text(state.error.asString(), color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { onEvent(AccountAssetEvent.OnRetry) }) {
                            Text(stringResource(R.string.acct_retry))
                        }
                    }
                }
            }

            state.bankAccounts.isEmpty() && state.eWallets.isEmpty() -> {
                // loading sudah selesai tapi tidak ada akun sama sekali —
                // dulu halaman tampil hampa tanpa keterangan apa pun
                Box(
                    Modifier
                            .fillMaxSize()
                            .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.acct_empty_title))
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.acct_empty_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { onEvent(AccountAssetEvent.OnAddAccountClick) }) {
                            Text(stringResource(R.string.acct_add_account))
                        }
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 16.dp),
                ) {
                    LazyColumn {

                        item {
                            TotalBalanceCard(
                                totalBalance = state.totalBalance,
                                isVisible = state.isBalanceVisible,
                                monthlyChangePercent = state.monthlyChangePercent,
                                activeAssetCount = state.activeAssetCount,
                                onToggleVisibility = { onEvent(AccountAssetEvent.OnToggleBalanceVisibility) },
                            )
                        }
                        state.bankAccounts.forEach { group ->
                            stickyHeader(key = group.nameGroupRes) {
                                Box(
                                    modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White),
                                ) {
                                    // Semua akun grup sudah ikut dirender di daftar ini, dan
                                    // belum ada layar tujuan untuk "semua akun per tipe" →
                                    // action "Lihat Semua" sengaja tidak ditampilkan dulu.
                                    FormField(
                                        title = stringResource(group.nameGroupRes),
                                        icon = Icons.Outlined.AccountBalanceWallet,
                                    )
                                }
                            }
                            items(
                                group.listAccount,
                                key = {
                                    it.id ?: "${group.nameGroupRes}_${it.name}_${it.provider}"
                                },
                            ) { items ->
                                AccountRow(
                                    account = items,
                                    onClick = { onEvent(AccountAssetEvent.OnAccountClick(items.id)) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TotalBalanceCard(
    totalBalance: Long,
    isVisible: Boolean,
    monthlyChangePercent: Double,
    activeAssetCount: Int,
    onToggleVisibility: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Flag satu arah (hanya true) disengaja: flag ini memilih formatCompact()
    // vs formatCurrency() di toFormatCurrencyVisibility. Bila dibalik otomatis,
    // teks berubah panjang → pendek → panjang lagi dan layout-nya loop.
    var isTotalSaldoOverflow: Boolean by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            PrimaryCyan,
                            SecondaryDeepPurple,

                            ),
                    ),
                )
                .padding(24.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.acct_total_balance_combined),
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                )
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.25f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        AppCurrencyHolder.value.code,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = totalBalance.toFormatCurrencyVisibility(isVisible, isTotalSaldoOverflow),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    onTextLayout = { result ->
                        if (result.hasVisualOverflow) {
                            isTotalSaldoOverflow = result.hasVisualOverflow
                        }
                    },
                )

                IconCircular(
                    icon = Icons.Outlined.Visibility,
                    iconColor = Color.White,
                    backgroundColor = Color.White.copy(alpha = 0.1f),
                    iconToCountainerRatio = 2,
                    onClick = onToggleVisibility,
                )
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color.White.copy(0.5f))
            Spacer(Modifier.height(12.dp))

            Row {
                Column {
                    Text(
                        stringResource(R.string.acct_this_month),
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 10.sp,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Naik = hijau mint, turun = merah muda; ikon & angka pakai warna yang sama
                        val isUp = monthlyChangePercent >= 0.0
                        val changeColor = if (isUp) Color(0xFFB9F6CA) else Color(0xFFFFCDD2)
                        Icon(
                            if (isUp) Icons.Filled.TrendingUp else Icons.Filled.TrendingDown,
                            contentDescription = null,
                            tint = changeColor,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            // Tanpa prefix "+" bawaan — nilai negatif sudah membawa tanda "-"
                            text = buildString {
                                append(if (isUp) "+" else "")
                                append(String.format("%.2f", monthlyChangePercent) + "%")
                            },
                            color = changeColor,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(Modifier.width(24.dp))
                Column {
                    Text(
                        stringResource(R.string.acct_active_assets),
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 10.sp,
                    )
                    Text(
                        stringResource(R.string.acct_active_account_count, activeAssetCount),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }

}


// ---------- Account row (bank / e-wallet) ----------

@Composable
private fun AccountRow(
    account: AccountUiModel,
    onClick: () -> Unit,
) {

    Row(
        modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = Color.Black.copy(alpha = 0.5f),
                    spotColor = Color.Black.copy(alpha = 0.5f),
                )
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(PrimaryCyan),
        )
        Row(
            modifier = Modifier
                    .weight(1f)
                    .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Icon & warna diambil dari provider akun: tipe akun menentukan icon,
            // warna brand bank/e-wallet menentukan background → konsisten dengan
            // AccountBottomSheetContent.
            val typeIcon = account.provider?.typeAccount?.toUiItem()?.icon
                ?: Icons.Outlined.AccountBalanceWallet

            Box(
                modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            (account.provider?.color ?: MaterialTheme.colorScheme.surfaceVariant).copy(alpha = 0.1f)
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = typeIcon,
                    contentDescription = null,
                    tint = (account.provider?.color ?: MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(account.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(
                    account.provider?.name ?: stringResource(R.string.acct_provider_undefined),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                text = account.balance.formatCurrency(),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
            )

            Spacer(Modifier.width(4.dp))
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = TextTertiary,
            )
        }
    }
}


// -----------
// PREVIEWS
// ---------------------------------------------------------------------------


@Preview(showBackground = true, name = "Content - Loaded")
@Composable
private fun AccountAssetContentLoadedPreview() {
    AccountAssetContent(
        state = AccountAssetState(
            isLoading = false,
            error = null,
            totalBalance = 15_750_000L,
            isBalanceVisible = true,
            monthlyChangePercent = 4.2,
            activeAssetCount = 5,
            bankAccounts = listOf(
                AccountUiGroupModel(
                    nameGroupRes = TypeAccount.BANK.labelResId,
                    listAccount = listOf(
                        AccountUiModel(
                            id = "1",
                            name = "BCA Utama",
                            provider = AccountProviderUiModel(
                                "kad",
                                "BCA",
                                TypeAccount.BANK,
                                Color.Red,
                            ),
                            balance = 8_500_000L,
                        ),
                        AccountUiModel(
                            id = "2",
                            name = "Mandiri Tabungan",
                            provider = AccountProviderUiModel(
                                "kad",
                                "BCA",
                                TypeAccount.BANK,
                                Color.Red,
                            ),

                            balance = 3_200_000L,
                        ),
                    ),
                ),
                AccountUiGroupModel(
                    nameGroupRes = TypeAccount.CARD.labelResId,
                    listAccount = listOf(
                        AccountUiModel(
                            id = "3",
                            name = "BCA Utama",
                            provider = AccountProviderUiModel(
                                "kad",
                                "BCA",
                                TypeAccount.BANK,
                                Color.Red,
                            ),

                            balance = 8_500_000L,
                        ),
                        AccountUiModel(
                            id = "4",
                            name = "Mandiri Tabungan",
                            provider = AccountProviderUiModel(
                                "kad",
                                "BCA",
                                TypeAccount.BANK,
                                Color.Red,
                            ),

                            balance = 3_200_000L,
                        ),
                    ),
                ),
            ),
            eWallets = listOf(
                AccountUiModel(
                    id = "5",
                    name = "GoPay",
                    provider = AccountProviderUiModel("kad", "BCA", TypeAccount.BANK, Color.Red),

                    balance = 450_000L,
                ),
            ),
        ),
        onEvent = {},
        snackbarHostState = remember { SnackbarHostState() },
    )
}

@Preview(showBackground = true, name = "Content - Loading")
@Composable
private fun AccountAssetContentLoadingPreview() {
    AccountAssetContent(
        state = AccountAssetState(
            isLoading = true,
            error = null,
            totalBalance = 0L,
            isBalanceVisible = true,
            monthlyChangePercent = 0.0,
            activeAssetCount = 0,
            bankAccounts = emptyList(),
            eWallets = emptyList(),
        ),
        onEvent = {},
        snackbarHostState = remember { SnackbarHostState() },
    )
}

@Preview(showBackground = true, name = "Content - Error")
@Composable
private fun AccountAssetContentErrorPreview() {
    AccountAssetContent(
        state = AccountAssetState(
            isLoading = false,
            error = UiText.Raw("Gagal memuat data akun. Periksa koneksi internet."),
            totalBalance = 0L,
            isBalanceVisible = true,
            monthlyChangePercent = 0.0,
            activeAssetCount = 0,
            bankAccounts = emptyList(),
            eWallets = emptyList(),
        ),
        onEvent = {},
        snackbarHostState = remember { SnackbarHostState() },
    )
}

@Preview(showBackground = true, widthDp = 360, name = "TotalBalanceCard")
@Composable
private fun TotalBalanceCardPreview() {
    Box(Modifier.padding(16.dp)) {
        TotalBalanceCard(
            totalBalance = 15_750_000L,
            isVisible = true,
            monthlyChangePercent = 4.2,
            activeAssetCount = 5,
            onToggleVisibility = {},
        )
    }
}

