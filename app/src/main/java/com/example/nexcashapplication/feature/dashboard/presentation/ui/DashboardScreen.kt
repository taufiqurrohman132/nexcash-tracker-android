package com.example.nexcashapplication.feature.dashboard.presentation.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.nexcashapplication.core.component.FormField
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.dashboard.presentation.event.DashboardEvent
import com.example.nexcashapplication.feature.dashboard.presentation.state.DashboardState
import com.example.nexcashapplication.feature.dashboard.presentation.ui.components.BalanceCard
import com.example.nexcashapplication.feature.dashboard.presentation.ui.components.BudgetCard
import com.example.nexcashapplication.feature.dashboard.presentation.ui.components.HeaderSection
import com.example.nexcashapplication.feature.dashboard.presentation.ui.components.QuickActionSection
import com.example.nexcashapplication.core.component.TransactionItem
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.PrimaryCyan

@Composable
fun DashboardScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                DashboardEffect.NavigateToBudgetDetail ->
                    navController.navigate(Route.BudgetRoute.EditMultiple)

                is DashboardEffect.NavigateToTransactionDetail ->
                    navController.navigate(Route.TransactionRoute.Detail(effect.transactionId))

                DashboardEffect.NavigateToAllTransactions ->
                    navController.navigate(Route.TransactionRoute.Main)

                DashboardEffect.NavigateToAddTransaction ->
                    navController.navigate(Route.TransactionRoute.AddEdit())

                DashboardEffect.NavigateToNotifications ->
                    navController.navigate(Route.SettingsRoute.Notifikasi)

                DashboardEffect.NavigateToProfile ->
                    navController.navigate(Route.SettingsRoute.EditProfile)

                // Efek lain (transfer, tagihan, scan, lainnya) belum punya tujuan
                else -> Unit
            }
        }
    }

    DashboardContent(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
}

@Preview
@Composable
fun DashboardScreenPreview() {
    DashboardScreen(
        navController = rememberNavController(),
    )
}

@Composable
fun DashboardContent(
    state: DashboardState,
    onEvent: (DashboardEvent) -> Unit,
    modifier: Modifier = Modifier
) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = PrimaryCyan,
                onClick = { onEvent(DashboardEvent.OnAddTransactionClick) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        },

        ) { padding ->


        Column(
            modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
        ) {

            HeaderSection(
                userName = state.userName,
                onClickNotification = {
                    onEvent(DashboardEvent.OnNotificationClick)
                },
                onClickProfile = {
                    onEvent(DashboardEvent.OnProfileClick)
                }
            )

            BalanceCard(
                totalBalance = state.totalBalance,
                income = state.income,
                expense = state.expense,
                isVisible = state.isBalanceVisible,
                onEyeClick = {
                    onEvent(DashboardEvent.OnToggleBalanceVisibility)
                }
            )

            QuickActionSection(
                modifier = Modifier.padding(vertical = 16.dp),
                onTransferClick = { onEvent(DashboardEvent.OnTransferClick) },
                onTagihanClick = { onEvent(DashboardEvent.OnTagihanClick) },
                onScanClick = { onEvent(DashboardEvent.OnScanClick) },
                onLainnyaClick = { onEvent(DashboardEvent.OnLainnyaClick) },
            )

            BudgetCard(
                remainingBudget = state.remainingBudget,
                progress = state.budgetUsedPercentage,
                onDetailClick = { onEvent(DashboardEvent.OnLihatDetailBudgetClick) },
            )

            FormField(
                title = "Transaksi Terakhir",
                actionText = "Semua",
                onActionClick = { onEvent(DashboardEvent.OnSemuaTransaksiClick) },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .border(
                                width = 1.dp,
                                color = BorderGray,
                                shape = RoundedCornerShape(16.dp)
                            )
                ) {
                    state.transactions.take(5).forEachIndexed { index, transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onClick = {
                                // id bisa null (data mapper) → event hanya dikirim bila ada
                                transaction.id?.let { id ->
                                    onEvent(DashboardEvent.OnTransactionClick(id))
                                }
                            }
                        )

                        if (index != state.transactions.take(5).lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}