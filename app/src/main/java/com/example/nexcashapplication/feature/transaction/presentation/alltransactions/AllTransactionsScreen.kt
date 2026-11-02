package com.example.nexcashapplication.feature.transaction.presentation.alltransactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel
import com.example.nexcashapplication.core.component.TransactionItem
import com.example.nexcashapplication.core.i18n.asString
import com.example.nexcashapplication.core.i18n.uiText
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.transaction.presentation.main.TransactionGroupHeader
import com.example.nexcashapplication.feature.transaction.presentation.main.component.TransactionEmptyScreen
import com.example.nexcashapplication.feature.transaction.presentation.main.model.TransactionHeaderUiModel
import com.example.nexcashapplication.feature.transaction.presentation.main.model.TransactionUiGroupModel
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme

/**
 * Halaman "Semua Transaksi" — satu layar untuk semua titik "Lihat Semua"
 * (by kategori, by akun, tanpa filter). Filter dibawa lewat route
 * `Route.TransactionRoute.All(categoryId, accountId)` (ADR-002).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTransactionsScreen(
    navController: NavHostController,
    viewModel: AllTransactionsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AllTransactionsEffect.NavigateBack -> navController.popBackStack()

                is AllTransactionsEffect.NavigateToTransactionDetail -> navController.navigate(
                    Route.TransactionRoute.Detail(effect.transactionId),
                )

                AllTransactionsEffect.NavigateToAddTransaction -> navController.navigate(
                    Route.TransactionRoute.AddEdit,
                )
            }
        }
    }

    AllTransactionsContent(
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTransactionsContent(
    state: AllTransactionsState,
    onEvent: (AllTransactionsEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.title.asString()) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(AllTransactionsEvent.OnBackClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.tx_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        val error = state.error
        when {
            state.isLoading -> Spacer(
                modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
            )

            error != null -> Column(
                modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = error.asString(),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = { onEvent(AllTransactionsEvent.OnRetryClick) }) {
                    Text(stringResource(R.string.tx_retry))
                }
            }

            state.groupedTransactions.isEmpty() -> TransactionEmptyScreen(
                onAddClick = { onEvent(AllTransactionsEvent.OnAddTransactionClick) },
                modifier = Modifier.padding(padding),
            )

            else -> LazyColumn(
                modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                contentPadding = PaddingValues(bottom = 24.dp),
            ) {
                state.groupedTransactions.forEach { group ->
                    val isExpanded = !state.collapsedGroups.contains(group.header.id)

                    stickyHeader(key = group.header.id) {
                        TransactionGroupHeader(
                            label = group.header.title,
                            count = group.listTransaction.size,
                            total = group.header.totalAmount,
                            isExpanded = isExpanded,
                            onToggle = {
                                onEvent(AllTransactionsEvent.OnGroupToggle(group.header.id))
                            },
                        )
                    }

                    if (isExpanded) {
                        items(
                            group.listTransaction,
                            key = { it.id ?: it.hashCode() },
                        ) { item ->
                            Column {
                                TransactionItem(
                                    transaction = item,
                                    onClick = { id ->
                                        if (id != null) {
                                            onEvent(AllTransactionsEvent.OnTransactionClick(id))
                                        }
                                    },
                                )
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Data contoh untuk preview saja — bukan data produksi.
private val sampleGroups = listOf(
    TransactionUiGroupModel(
        header = TransactionHeaderUiModel(
            id = "2026-09-24",
            title = "Hari ini",
            time = "12:30",
            totalAmount = -65_000,
            isExpanded = true,
        ),
        listTransaction = listOf(
            TransactionItemUiModel(
                id = "1",
                name = "Nasi Goreng",
                time = "12:30",
                amount = 25_000,
                typeTransaction = TypeCategory.EXPENSE,
                category = "Makanan",
                account = "BCA",
                icon = Icons.Default.Fastfood,
                iconColor = Color.Black,
            ),
            TransactionItemUiModel(
                id = "2",
                name = "Ongkir Motor",
                time = "09:15",
                amount = 40_000,
                typeTransaction = TypeCategory.EXPENSE,
                category = "Transportasi",
                account = "BCA",
                icon = Icons.Default.CreditCard,
                iconColor = Color.Black,
            ),
        ),
    ),
)

@Preview(showBackground = true)
@Composable
private fun AllTransactionsContentPreview() {
    NexCashApplicationTheme {
        AllTransactionsContent(
            state = AllTransactionsState(
                isLoading = false,
                title = uiText(R.string.tx_title_filter, "Makanan"),
                groupedTransactions = sampleGroups,
            ),
            onEvent = {},
        )
    }
}
