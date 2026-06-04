package com.example.nexcashapplication.feature.dashboard.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nexcashapplication.feature.dashboard.presentation.event.DashboardEvent
import com.example.nexcashapplication.feature.dashboard.presentation.state.DashboardState
import com.example.nexcashapplication.feature.dashboard.presentation.ui.components.BalanceCard
import com.example.nexcashapplication.feature.dashboard.presentation.ui.components.BudgetCard
import com.example.nexcashapplication.feature.dashboard.presentation.ui.components.HeaderSection
import com.example.nexcashapplication.feature.dashboard.presentation.ui.components.QuickActionSection
import com.example.nexcashapplication.feature.dashboard.presentation.ui.components.RecentTransactionSection
import com.example.nexcashapplication.feature.dashboard.presentation.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        },

        ) { padding ->

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                HeaderSection(
                    userName = state.userName
                )
            }

            item {
                BalanceCard(
                    totalBalance = state.totalBalance,
                    income = state.income,
                    expense = state.expense,
                    isVisible = state.isBalanceVisible,
                    onEyeClick = {
                        onEvent(
                            DashboardEvent.ToggleBalanceVisibility
                        )
                    }
                )
            }

            item {
                QuickActionSection()
            }

            item {
                BudgetCard(
                    remainingBudget = state.remainingBudget,
                    progress = state.budgetUsedPercentage
                )
            }

            item {
                RecentTransactionSection(
                    transactions = state.transactions
                )
            }
        }
    }

}