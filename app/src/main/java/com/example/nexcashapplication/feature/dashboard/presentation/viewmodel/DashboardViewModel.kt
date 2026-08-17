package com.example.nexcashapplication.feature.dashboard.presentation.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Work
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel
import com.example.nexcashapplication.feature.dashboard.presentation.event.DashboardEvent
import com.example.nexcashapplication.feature.dashboard.presentation.state.DashboardState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor() :
    BaseViewModel<DashboardState, DashboardEvent, Unit>(
        DashboardState(
            transactions = sampleTransactions(),
        ),
    ) {

    override fun onEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.ToggleBalanceVisibility -> {
                updateState {
                    it.copy(
                        isBalanceVisible = !it.isBalanceVisible,
                    )
                }
            }

            else -> Unit
        }
    }

    companion object {
        private fun sampleTransactions() =
            listOf(
                TransactionItemUiModel(
                    id = "1",
                    title = "Gaji Bulanan",
                    time = "08:00",
                    amount = 8_500_000,
                    isIncome = true,
                    category = "Gaji",
                    account = "BCA",
                    icon = Icons.Default.AccountBalanceWallet,
                ),
                TransactionItemUiModel(
                    id = "2",
                    title = "Makan Siang",
                    time = "12:30",
                    amount = 35_000,
                    isIncome = false,
                    category = "Makanan & Minuman",
                    account = "Cash",
                    icon = Icons.Default.Restaurant,
                ),
                TransactionItemUiModel(
                    id = "3",
                    title = "Langganan Netflix",
                    time = "10:15",
                    amount = 54_000,
                    isIncome = false,
                    category = "Hiburan",
                    account = "Gopay",
                    icon = Icons.Default.Movie,
                ),
                TransactionItemUiModel(
                    id = "4",
                    title = "Bensin Motor",
                    time = "07:45",
                    amount = 25_000,
                    isIncome = false,
                    category = "Transportasi",
                    account = "Cash",
                    icon = Icons.Default.LocalGasStation,
                ),
                TransactionItemUiModel(
                    id = "5",
                    title = "Freelance Project",
                    time = "16:20",
                    amount = 1_200_000,
                    isIncome = true,
                    category = "Freelance",
                    account = "BCA",
                    icon = Icons.Default.Work,
                ),
                TransactionItemUiModel(
                    id = "6",
                    title = "Belanja Bulanan",
                    time = "19:00",
                    amount = 450_000,
                    isIncome = false,
                    category = "Belanja",
                    account = "Gopay",
                    icon = Icons.Default.Shop,
                ),
                TransactionItemUiModel(
                    id = "7",
                    title = "Tagihan Listrik",
                    time = "09:10",
                    amount = 180_000,
                    isIncome = false,
                    category = "Tagihan",
                    account = "BCA",
                    icon = Icons.Default.Bolt,
                ),
                TransactionItemUiModel(
                    id = "8",
                    title = "Transfer dari Teman",
                    time = "14:05",
                    amount = 200_000,
                    isIncome = true,
                    category = "Tanpa Kategori",
                    account = "Akun Dihapus",
                    icon = Icons.Default.Category,
                ),
            )
    }
}
