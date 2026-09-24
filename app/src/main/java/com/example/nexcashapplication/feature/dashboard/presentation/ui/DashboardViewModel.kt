package com.example.nexcashapplication.feature.dashboard.presentation.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.dashboard.presentation.event.DashboardEvent
import com.example.nexcashapplication.feature.dashboard.presentation.state.DashboardState
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType
import com.example.nexcashapplication.feature.transaction.presentation.main.mapper.toItemUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
) : BaseViewModel<DashboardState, DashboardEvent, DashboardEffect>(
    DashboardState(),
) {

    init {
        loadData()
    }

    override fun onEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.OnAddTransactionClick -> TODO()
            DashboardEvent.OnLainnyaClick -> TODO()
            DashboardEvent.OnLihatDetailBudgetClick -> {
                viewModelScope.launch {
                    sendEffect(DashboardEffect.NavigateToBudgetDetail)
                }
            }
            DashboardEvent.OnNotificationClick -> TODO()
            DashboardEvent.OnProfileClick -> TODO()
            DashboardEvent.OnRefresh -> TODO()
            DashboardEvent.OnScanClick -> TODO()
            DashboardEvent.OnScreenLoad -> TODO()
            DashboardEvent.OnSemuaTransaksiClick -> TODO()
            DashboardEvent.OnTagihanClick -> TODO()
            DashboardEvent.OnToggleBalanceVisibility -> {
                updateState {
                    it.copy(
                        isBalanceVisible = !it.isBalanceVisible,
                    )
                }
            }

            is DashboardEvent.OnTransactionClick -> {
                viewModelScope.launch {
                    sendEffect(
                        DashboardEffect.NavigateToTransactionDetail(
                            transactionId = event.transactionId,
                        ),
                    )
                }
            }

            DashboardEvent.OnTransferClick -> TODO()
        }
    }

    fun loadData() {
        transactionRepository.getRecentTransactionsByCategory(5, "")
                .onEach { transactionsWithDetail ->
                    updateState { dashboardState ->
                        dashboardState.copy(
                            transactions = transactionsWithDetail.map { it.toItemUiModel() },
                        )
                    }
                }
                .launchIn(viewModelScope)
    }

    companion object {
        private fun sampleTransactions() =
            listOf(
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
                    icon = Icons.Default.LocalGasStation,
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
                    icon = Icons.Default.Shop,
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
    }
}