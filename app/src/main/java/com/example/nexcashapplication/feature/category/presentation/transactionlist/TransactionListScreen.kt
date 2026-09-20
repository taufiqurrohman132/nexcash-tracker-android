package com.example.nexcashapplication.feature.category.presentation.transactionlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.nexcashapplication.core.component.TransactionItem
import com.example.nexcashapplication.core.domain.model.CategoryIcon
import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel
import com.example.nexcashapplication.core.domain.model.TypeCategory.EXPENSE
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.category.presentation.detail.SubcategoryGrid
import com.example.nexcashapplication.feature.category.presentation.shared.model.CategoryUiItemModel
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    navHostController: NavHostController,
    viewModel: TransactionListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is TransactionListEffect.NavigateBack -> navHostController.popBackStack()
                is TransactionListEffect.NavigateToTransactionDetail -> {
                    navHostController.navigate(
                        Route.TransactionRoute.Detail(
                            effect.transactionId
                        )
                    )
                }
            }
        }
    }

    TransactionListContent(
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListContent(
    state: TransactionListState,
    onEvent: (TransactionListEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaksi · ${state.categoryName}") },
                navigationIcon = {
                    IconButton(onClick = { onEvent(TransactionListEvent.OnBackClick) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding)
                    .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(state.transactions, key = { it.id ?: it.hashCode()}){
                TransactionItem(
                    transaction = it,
                    onClick = { transactionId -> onEvent(TransactionListEvent.OnTransactionClick(transactionId ?: "")) },
                )
            }
        }
    }
}

val dummyTransactions = listOf(
    TransactionItemUiModel(
        id = "1",
        name = "Gaji Bulanan",
        time = "08:00",
        amount = 8_500_000,
        typeTransaction = TransactionType.EXPENSE,
        category = "Gaji",
        account = "BCA",
        icon = Icons.Default.CreditCard,
        iconColor = Color.Black,
    ) ,
    TransactionItemUiModel(
        id = "2",
        name = "Gaji Bulanan",
        time = "08:00",
        amount = 8_500_000,
        typeTransaction = TransactionType.EXPENSE,
        category = "Gaji",
        account = "BCA",
        icon = Icons.Default.CreditCard,
        iconColor = Color.Black,
    )
)

@Preview(showBackground = true)
@Composable
private fun TransactionListContentPreview() {
    NexCashApplicationTheme {
        TransactionListContent(
            state = TransactionListState(
                isLoading = false,
                transactions = dummyTransactions,
            ),
            onEvent = {},
        )
    }
}
