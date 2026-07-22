package com.example.nexcashapplication.feature.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.domain.model.TransactionItemUi
import com.example.nexcashapplication.feature.dashboard.presentation.event.DashboardEvent
import com.example.nexcashapplication.feature.dashboard.presentation.state.DashboardState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DashboardViewModel(

): ViewModel() {
    private val _state= MutableStateFlow(
        DashboardState(
            transactions = sampleTransactions()
        )
    )
    val state: StateFlow<DashboardState> =
        _state.asStateFlow()

    fun onEvent(event: DashboardEvent){
        when(event){
            DashboardEvent.ToggleBalanceVisibility -> {
                _state.update {
                    it.copy(
                        isBalanceVisible = !it.isBalanceVisible
                    )
                }
            }
            else -> Unit
        }
    }

    companion object {

        private fun sampleTransactions() =
            listOf(
                TransactionItemUi(
                    id = "1",
                    title = "Gaji Bulanan",
                    subtitle = "BCA",
                    time = "08:00",
                    amount = 5_000_000,
                    isIncome = true,
                    category = "Salary",
                    iconRes = R.drawable.insights
                ),
                TransactionItemUi(
                    id = "2",
                    title = "Makan Siang",
                    subtitle = "GoPay",
                    time = "12:30",
                    amount = 35_000,
                    isIncome = false,
                    category = "Food",
                    iconRes = R.drawable.transaction
                ),
                TransactionItemUi(
                    id = "3",
                    title = "Isi Bensin",
                    subtitle = "OVO",
                    time = "17:15",
                    amount = 50_000,
                    isIncome = false,
                    category = "Transport",
                    iconRes = R.drawable.home
                ),
                TransactionItemUi(
                    id = "4",
                    title = "Freelance Project",
                    subtitle = "SeaBank",
                    time = "20:45",
                    amount = 1_500_000,
                    isIncome = true,
                    category = "Freelance",
                    iconRes = R.drawable.eye_show
                ),
                TransactionItemUi(
                    id = "5",
                    title = "Belanja Bulanan",
                    subtitle = "ShopeePay",
                    time = "19:20",
                    amount = 250_000,
                    isIncome = false,
                    category = "Shopping",
                    iconRes = R.drawable.category_list
                )
            )
    }
}