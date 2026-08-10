package com.example.nexcashapplication.feature.aset.presentation.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.component.FormField
import com.example.nexcashapplication.core.domain.model.TransactionItemUi
import com.example.nexcashapplication.core.extension.formatCurrency
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
                DetailAccountEffect.NavigateBack -> TODO()
                DetailAccountEffect.NavigateBackAfterDelete -> TODO()
                is DetailAccountEffect.NavigateToAddTransaction -> TODO()
                is DetailAccountEffect.NavigateToAllTransactions -> TODO()
                is DetailAccountEffect.NavigateToAssetSettings -> TODO()
                is DetailAccountEffect.NavigateToEditAssetName -> TODO()
                is DetailAccountEffect.NavigateToTransactionDetail -> TODO()
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
                title = { Text("Akun & Aset", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.Add, contentDescription = "Tambah akun")
                    }
                },
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
                        actionText = "Lihat Semua"
                    ){
                        RecentTransactionsSection(state.recentTransactions, onEvent)
                    }
                    Spacer(Modifier.height(24.dp))
                    AssetSettingsSection(onEvent)
                    Spacer(Modifier.height(24.dp))
                }
            }


        }
        if (state.showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { onEvent(DetailAccountEvent.OnDismissDeleteDialog) },
                title = { Text("Hapus Aset Ini?") },
                text = { Text("Tindakan ini akan menghapus \"${state.assetName}\" beserta seluruh transaksi terkait. Tindakan ini tidak dapat dibatalkan.") },
                confirmButton = {
                    TextButton(
                        onClick = { onEvent(DetailAccountEvent.OnConfirmDelete) },
                    ) { Text("Hapus", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = { onEvent(DetailAccountEvent.OnDismissDeleteDialog) }) {
                        Text("Batal")
                    }
                },
            )
        }
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
            onClick = { onEvent(DetailAccountEvent.OnUbahNamaAsetClick) },
        )
        Spacer(Modifier.height(10.dp))
        SettingsRow(
            icon = Icons.Default.DeleteOutline,
            label = "Hapus Aset Ini",
            tint = Color(0xFFE6455B),
            background = Color(0xFFFCEEF0),
            onClick = { onEvent(DetailAccountEvent.OnHapusAsetClick) },
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
    transactions: List<TransactionItemUi>,
    onEvent: (DetailAccountEvent) -> Unit,
) {

        Column(
            modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFFEFEFEF), RoundedCornerShape(16.dp)),
        ) {
            transactions.forEachIndexed { index, tx ->
                TransactionRow(tx) { onEvent(DetailAccountEvent.OnTransactionClick(tx.id)) }
                if (index != transactions.lastIndex) {
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                }
            }
        }

}


@Composable
private fun TransactionRow(tx: TransactionItemUi, onClick: () -> Unit) {
    Row(
        modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (tx.amount >= 0) Color(0xFFE1F7EE) else Color(0xFFFCE8EC)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painterResource(tx.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(tx.title, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(tx.time, fontSize = 11.sp, color = Color(0xFF999999))
            }
        }
        Text(
            text = (if (tx.amount >= 0) "+ " else "- ") + tx.amount.formatCurrency(),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (tx.amount >= 0) Color(0xFF1FA971) else Color(0xFFE6455B),
        )
    }
}


@Composable
private fun ActionButtons(onEvent: (DetailAccountEvent) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = { onEvent(DetailAccountEvent.OnTransaksiClick) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF29C5E0)),
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Transaksi")
        }
        OutlinedButton(
            onClick = { onEvent(DetailAccountEvent.OnTransferClick) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Color(0xFF29C5E0)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF29C5E0)),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text("Transfer")
        }
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
                label = { Text(state.accountLabel, fontSize = 12.sp) },
                colors = AssistChipDefaults.assistChipColors(containerColor = Color.White),
                modifier = Modifier.height(28.dp) // paksa lebih pendek dari minimum touch target
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = state.balance.formatCurrency(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A2B2E),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "${state.bankName} • ${state.assetName}",
            fontSize = 12.sp,
            color = Color(0xFF5B6B70),
        )
    }

}

// AssetDetailScreenPreview.kt

private val dummyTransactions = listOf(
    TransactionItemUi(
        id = "1L",
        title = "Gaji Bulanan",
        time = "05 Agu 2026",
        amount = 5_000_000L,
        iconRes = R.drawable.transaction, // ganti sama drawable asli project kamu
        subtitle = "kdlkfldkf",
        isIncome = true,
        category = "income",
    ),
    TransactionItemUi(
        id = "1L",
        title = "Gaji Bulanan",
        time = "05 Agu 2026",
        amount = 5_000_000L,
        iconRes = R.drawable.transaction, // ganti sama drawable asli project kamu
        subtitle = "kdlkfldkf",
        isIncome = true,
        category = "income",
    ),
    TransactionItemUi(
        id = "1L",
        title = "Gaji Bulanan",
        time = "05 Agu 2026",
        amount = 5_000_000L,
        iconRes = R.drawable.transaction, // ganti sama drawable asli project kamu
        subtitle = "kdlkfldkf",
        isIncome = true,
        category = "income",
    ),
)

private val dummyState = DetailAccountState(
    isLoading = false,
    assetName = "BCA Utama",
    accountLabel = "Tabungan",
    balance = 12_450_000L,
    bankName = "Bank Central Asia",
    accountNumber = "1234567890",
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