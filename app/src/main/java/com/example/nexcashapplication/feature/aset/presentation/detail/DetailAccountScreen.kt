package com.example.nexcashapplication.feature.aset.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.nexcashapplication.core.component.TransactionItem
import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.aset.domain.model.TypeAccount
import com.example.nexcashapplication.feature.aset.presentation.addedit.form.model.AccountProviderUiModel
import com.example.nexcashapplication.feature.aset.presentation.main.mapper.labelResId
import com.example.nexcashapplication.feature.aset.presentation.shared.component.DeleteAccountDialog
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DetailAccountScreen(
    viewModel: DetailAccountViewModel = hiltViewModel(),
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                DetailAccountEffect.NavigateBack -> navController.popBackStack()
                DetailAccountEffect.NavigateBackAfterDelete -> {
                    navController.popBackStack()
                }

                is DetailAccountEffect.NavigateToAddTransaction -> TODO()
                is DetailAccountEffect.NavigateToAllTransactions -> TODO()
                is DetailAccountEffect.NavigateToAssetSettings -> TODO()
                is DetailAccountEffect.NavigateToEditAssetName -> navController.navigate(
                    Route.AccountRoute.AddEditInfo(accountId = effect.assetId),
                )

                is DetailAccountEffect.NavigateToTransactionDetail -> {

                }

                is DetailAccountEffect.NavigateToTransfer -> TODO()
                is DetailAccountEffect.ShowSnackbar -> TODO()
            }
        }
    }

    DetailAssetContent(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailAssetContent(
    state: DetailAccountState,
    onEvent: (DetailAccountEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.assetName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(DetailAccountEvent.OnBackClick) }) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },

        ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    Modifier
                            .fillMaxSize()
                            .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                if (state.isLoading) {
                    Box(
                        Modifier
                                .fillMaxSize()
                                .padding(padding),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                    return@Scaffold
                }

                Column(
                    modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp),
                ) {
                    Spacer(Modifier.height(16.dp))
                    BalanceCard(state)
                    Spacer(Modifier.height(16.dp))
                    ActionButtons(onEvent)
                    FormField(
                        title = "Transaksi terakhir",
                        icon = Icons.Default.Replay,
                        actionText = "Lihat Semua",
                    ) {
                        RecentTransactionsSection(
                            transactions = state.recentTransactions,
                            onItemClick = { id ->
                                onEvent(DetailAccountEvent.OnTransactionClick(id))
                            },
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    AssetSettingsSection(onEvent)
                    Spacer(Modifier.height(24.dp))
                }
            }


        }
        state.deleteDialogDesision?.let {
            DeleteAccountDialog(
                dialogDesision = it,
                onDismiss = { onEvent(DetailAccountEvent.OnDismissDeleteDialog) },
                onConfirm = { onEvent(DetailAccountEvent.OnConfirmDelete) },
            )
        }
//        if (state.showDeleteConfirmation) {
//            AlertDialog(
//                onDismissRequest = { onEvent(DetailAccountEvent.OnDismissDeleteDialog) },
//                title = { Text("Hapus Aset Ini?") },
//                text = { Text("Tindakan ini akan menghapus \"${state.assetName}\" beserta seluruh transaksi terkait. Tindakan ini tidak dapat dibatalkan.") },
//                confirmButton = {
//                    TextButton(
//                        onClick = { onEvent(DetailAccountEvent.OnConfirmDelete) },
//                    ) { Text("Hapus", color = MaterialTheme.colorScheme.error) }
//                },
//                dismissButton = {
//                    TextButton(onClick = { onEvent(DetailAccountEvent.OnDismissDeleteDialog) }) {
//                        Text("Batal")
//                    }
//                },
//            )
//        }
    }
}

@Composable
fun AssetSettingsSection(onEvent: (DetailAccountEvent) -> Unit) {
    Column {
        Text("PENGATURAN ASET", fontSize = 11.sp, color = Color(0xFF999999), letterSpacing = 0.5.sp)
        Spacer(Modifier.height(8.dp))
        SettingsRow(
            icon = Icons.Default.Edit,
            label = "Ubah Nama Aset",
            tint = Color(0xFF29C5E0),
            background = Color(0xFFF3FAFB),
            onClick = { onEvent(DetailAccountEvent.OnEditAccountClick) },
        )
        Spacer(Modifier.height(10.dp))
        SettingsRow(
            icon = Icons.Default.DeleteOutline,
            label = "Hapus Aset Ini",
            tint = Color(0xFFE6455B),
            background = Color(0xFFFCEEF0),
            onClick = { onEvent(DetailAccountEvent.OnDeleteAccountClick) },
        )
    }
}


@Composable
private fun SettingsRow(
    icon: ImageVector,
    label: String,
    tint: Color,
    background: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(background)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(label, color = tint, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}


@Composable
private fun RecentTransactionsSection(
    transactions: List<TransactionItemUiModel>,
    onItemClick: (String?) -> Unit,
) {

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = BorderGray,
                    shape = RoundedCornerShape(16.dp),
                ),
    ) {
        transactions.take(5).forEachIndexed { index, transaction ->
            TransactionItem(
                transaction = transaction,
                onClick = { id ->
                    onItemClick(id)
                },
            )

            if (index != transactions.take(5).lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = BorderGray,
                )
            }
        }
    }

}

@Composable
private fun ActionButtons(onEvent: (DetailAccountEvent) -> Unit) {
    Button(
        onClick = { onEvent(DetailAccountEvent.OnAddTransactionClick) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF29C5E0)),
    ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            "Transaksi",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}


@Composable
fun BalanceCard(
    state: DetailAccountState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(PrimaryCyan.copy(alpha = 0.1f))
                .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Total Saldo", fontSize = 13.sp, color = Color(0xFF5B6B70))
            AssistChip(
                onClick = {},
                shape = CircleShape,
                label = {
                    Text(
                        stringResource(state.typeAccount.labelResId),
                        fontSize = 12.sp,
                        color = PrimaryCyan,
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = PrimaryCyan.copy(alpha = 0.1f),
                ),
                border = AssistChipDefaults.assistChipBorder(
                    enabled = true,
                    borderColor = PrimaryCyan.copy(alpha = 0.5f),
                    borderWidth = 1.dp,
                ),
                modifier = Modifier.height(28.dp), // paksa lebih pendek dari minimum touch target
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.total_balance_format, state.balance.formatCurrency()),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A2B2E),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "${state.typeAccount.name} • ${state.providers?.name}",
            fontSize = 12.sp,
            color = Color(0xFF5B6B70),
        )
    }

}

// AssetDetailScreenPreview.kt
val dummyTransactions = listOf(
    TransactionItemUiModel(
        id = "1",
        name = "Gaji Bulanan",
        time = "08:00",
        amount = 8_500_000,
        typeTransaction = TransactionType.INCOME,
        category = "Gaji",
        account = "BCA",
        icon = Icons.Default.AccountBalanceWallet,
        iconColor = Color.Black,
    ),
    TransactionItemUiModel(
        id = "2",
        name = "Makan Siang",
        time = "12:30",
        amount = 35_000,
        typeTransaction = TransactionType.INCOME,
        category = "Makanan & Minuman",
        account = "Cash",
        icon = Icons.Default.Restaurant,
        iconColor = Color.Black,
    ),
    TransactionItemUiModel(
        id = "3",
        name = "Langganan Netflix",
        time = "10:15",
        amount = 54_000,
        typeTransaction = TransactionType.INCOME,
        category = "Hiburan",
        account = "Gopay",
        icon = Icons.Default.Movie,
        iconColor = Color.Black,
    ),
    TransactionItemUiModel(
        id = "4",
        name = "Bensin Motor",
        time = "07:45",
        amount = 25_000,
        typeTransaction = TransactionType.INCOME,
        category = "Transportasi",
        account = "Cash",
        icon = Icons.Default.LocationOn,
        iconColor = Color.Black,
    ),
    TransactionItemUiModel(
        id = "5",
        name = "Freelance Project",
        time = "16:20",
        amount = 1_200_000,
        typeTransaction = TransactionType.INCOME,
        category = "Freelance",
        account = "BCA",
        icon = Icons.Default.Work,
        iconColor = Color.Black,
    ),
    TransactionItemUiModel(
        id = "6",
        name = "Belanja Bulanan",
        time = "19:00",
        amount = 450_000,
        typeTransaction = TransactionType.INCOME,
        category = "Belanja",
        account = "Gopay",
        icon = Icons.Default.ShoppingCart,
        iconColor = Color.Black,
    ),
    TransactionItemUiModel(
        id = "7",
        name = "Tagihan Listrik",
        time = "09:10",
        amount = 180_000,
        typeTransaction = TransactionType.INCOME,
        category = "Tagihan",
        account = "BCA",
        icon = Icons.Default.Bolt,
        iconColor = Color.Black,
    ),
    TransactionItemUiModel(
        id = "8",
        name = "Transfer dari Teman",
        time = "14:05",
        amount = 200_000,
        typeTransaction = TransactionType.INCOME,
        category = "Tanpa Kategori",
        account = "Akun Dihapus",
        icon = Icons.Default.Category,
        iconColor = Color.Black,
    ),
)

private val dummyState = DetailAccountState(
    isLoading = false,
    assetName = "BCA Utama",
    typeAccount = TypeAccount.OTHER,
    balance = 12_450_000L,
    providers = AccountProviderUiModel("kd", "SEABANK", TypeAccount.DEBIT_CARD, Color.Red),
    recentTransactions = dummyTransactions,
    showDeleteConfirmation = false,
)

@Preview(showBackground = true)
@Composable
private fun AssetDetailContentPreview() {
    MaterialTheme {
        DetailAssetContent(
            state = dummyState,
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
private fun AssetDetailContentLoadingPreview() {
    MaterialTheme {
        DetailAssetContent(
            state = dummyState.copy(isLoading = true),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}

@Preview(showBackground = true, name = "Delete Confirmation")
@Composable
private fun AssetDetailContentDeleteDialogPreview() {
    MaterialTheme {
        DetailAssetContent(
            state = dummyState.copy(showDeleteConfirmation = true),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}

@Preview(showBackground = true, name = "Empty Transactions")
@Composable
private fun AssetDetailContentEmptyPreview() {
    MaterialTheme {
        DetailAssetContent(
            state = dummyState.copy(recentTransactions = emptyList()),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}