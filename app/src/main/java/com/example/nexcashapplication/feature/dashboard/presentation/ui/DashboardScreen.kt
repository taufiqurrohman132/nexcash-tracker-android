package com.example.nexcashapplication.feature.dashboard.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nexcashapplication.core.component.FormField
import com.example.nexcashapplication.feature.dashboard.presentation.event.DashboardEvent
import com.example.nexcashapplication.feature.dashboard.presentation.state.DashboardState
import com.example.nexcashapplication.feature.dashboard.presentation.ui.components.BalanceCard
import com.example.nexcashapplication.feature.dashboard.presentation.ui.components.BudgetCard
import com.example.nexcashapplication.feature.dashboard.presentation.ui.components.HeaderSection
import com.example.nexcashapplication.feature.dashboard.presentation.ui.components.QuickActionSection
import com.example.nexcashapplication.core.component.TransactionItem
import com.example.nexcashapplication.feature.dashboard.presentation.viewmodel.DashboardViewModel
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.PrimaryCyan

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
                containerColor = PrimaryCyan,
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
                .fillMaxSize(),
            contentPadding = padding,
//            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item(key = "HeaderSection") {
                HeaderSection(
                    userName = state.userName,
                    onClickNotification = {

                    },
                    onClickProfile = {

                    }
                )
            }

            item(key = "BalanceCard") {
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

            item(key = "QuickActionSection") {
                QuickActionSection(
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            item(key = "BudgetCard") {
                BudgetCard(
                    remainingBudget = state.remainingBudget,
                    progress = state.budgetUsedPercentage
                )
            }

            // Item transaksi langsung di LazyColumn
            item(key = "transaction_list_card") {

                FormField(
                    title = "Transaksi Terakhir",
                    actionText = "Semua",
                    modifier = Modifier.padding(horizontal = 16.dp)
                ){
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
                        // pakai LazyColumn nested? JANGAN
                        // Tapi kalau transaksi dibatasi misal max 5, Column fine
                        state.transactions.take(5).forEach { transaction ->
                            TransactionItem(transaction = transaction, onClick = {})
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                        }
                    }
                }



                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}