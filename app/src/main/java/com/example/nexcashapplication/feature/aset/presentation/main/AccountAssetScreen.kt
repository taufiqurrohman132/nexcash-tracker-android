package com.example.nexcashapplication.feature.aset.presentation.main

import android.widget.Toast
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
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
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.core.extension.toFormatCurrencyVisibility
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.aset.domain.model.TypeAccount
import com.example.nexcashapplication.feature.aset.presentation.main.model.AccountUiGroupModel
import com.example.nexcashapplication.feature.aset.presentation.main.model.AccountUiModel
import com.example.nexcashapplication.ui.theme.PrimaryCyan
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

                is AccountAssetEffect.NavigateToAllAccounts -> {

                }

                is AccountAssetEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    AccountAssetContent(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Akun & Aset", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onClick = { onEvent(AccountAssetEvent.OnAddAccountClick) },
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Tambah akun")
                    }
                },
            )
        },
    ) { padding ->
        when {
            state.isLoading && state.bankAccounts.isEmpty() && state.eWallets.isEmpty() -> {
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
                        Text(state.error, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { onEvent(AccountAssetEvent.OnRetry) }) { Text("Coba lagi") }
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
                            stickyHeader(key = group.nameGroup) {
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                            .background(Color.White)
                                ){
                                    FormField(
                                        title = group.nameGroup,
                                        icon = Icons.Outlined.AccountBalanceWallet,
                                        actionText = if (group.listAccount.size > 3) "Lihat Semua"
                                        else null,
                                    )
                                }
                            }
                            items(group.listAccount, key = { it.id }) { items ->
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
    var isTotalSaldoOverflow: Boolean by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(listOf(Color(0xFF29B6F6), Color(0xFF5C6BC0))),
                )
                .padding(24.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Total Saldo Gabungan",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                )
                Spacer(Modifier.width(6.dp))
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.25f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text("IDR", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(
                        R.string.total_balance_format,
                        totalBalance.toFormatCurrencyVisibility(isVisible, isTotalSaldoOverflow),
                    ),
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
                    Text("BULAN INI", color = Color.White.copy(alpha = 0.75f), fontSize = 10.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.TrendingUp,
                            contentDescription = null,
                            tint = Color(0xFFB9F6CA),
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            "+${monthlyChangePercent}%",
                            color = Color(0xFFB9F6CA),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(Modifier.width(24.dp))
                Column {
                    Text("ASET AKTIF", color = Color.White.copy(alpha = 0.75f), fontSize = 10.sp)
                    Text(
                        "$activeAssetCount Akun",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }

}


@Composable
private fun SectionHeader(
    title: String,
    actionLabel: String? = null,
    onActionClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (actionLabel != null) {
            Text(
                actionLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onActionClick),
            )
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
                    shape = RoundedCornerShape(12.dp),
                    ambientColor = Color.Black.copy(alpha = 0.5f),
                    spotColor = Color.Black.copy(alpha = 0.5f),
                )
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Color.White,
//                    Color.Red
                )

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
            Box(
                modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.AccountBalance, contentDescription = null)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(account.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(
                    account.provider,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                text = account.balance.formatCurrency(),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
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


// ---------------------------------------------------------------------------
// PREVIEWS
// Asumsi: AccountAssetState & AccountUiModel punya field ini — sesuaikan
// nama field kalau berbeda dari data class asli kamu (tidak ada di file
// yang kamu paste, jadi ini tebakan dari cara field itu dipakai).
// ---------------------------------------------------------------------------


val dummyAccounts = listOf(
    AccountUiModel(
        id = "acc-001",
        name = "Dompet Utama",
        provider = "Cash",
        balance = 2_500_000L,
        iconUrl = null,
    ),
    AccountUiModel(
        id = "acc-002",
        name = "BCA Tabungan",
        provider = "BCA",
        balance = 15_750_000L,
        iconUrl = "https://example.com/bca.png",
    ),
    AccountUiModel(
        id = "acc-003",
        name = "GoPay",
        provider = "Gojek",
        balance = 350_000L,
        iconUrl = "https://example.com/gopay.png",
    ),
    AccountUiModel(
        id = "acc-004",
        name = "OVO",
        provider = "OVO",
        balance = 125_000L,
        iconUrl = "https://example.com/ovo.png",
    ),
    AccountUiModel(
        id = "acc-005",
        name = "Mandiri Syariah",
        provider = "BSI",
        balance = 8_000_000L,
        iconUrl = null,
    ),
)

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
                    nameGroup = TypeAccount.BANK.name,
                    listAccount = listOf(
                        AccountUiModel(
                            id = "1",
                            name = "BCA Utama",
                            provider = "BCA",
                            balance = 8_500_000L,
                        ),
                        AccountUiModel(
                            id = "2",
                            name = "Mandiri Tabungan",
                            provider = "Mandiri",
                            balance = 3_200_000L,
                        ),
                    ),
                ),
                AccountUiGroupModel(
                    nameGroup = TypeAccount.CARD.name,
                    listAccount = listOf(
                        AccountUiModel(
                            id = "3",
                            name = "BCA Utama",
                            provider = "BCA",
                            balance = 8_500_000L,
                        ),
                        AccountUiModel(
                            id = "4",
                            name = "Mandiri Tabungan",
                            provider = "Mandiri",
                            balance = 3_200_000L,
                        ),
                    ),
                ),

                ),
            eWallets = listOf(
                AccountUiModel(
                    id = "3",
                    name = "GoPay",
                    provider = "GoPay",
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
            error = "Gagal memuat data akun. Periksa koneksi internet.",
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

