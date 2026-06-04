package com.example.nexcashapplication.feature.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.nexcashapplication.feature.dashboard.presentation.event.DashboardEvent
import com.example.nexcashapplication.feature.dashboard.presentation.state.DashboardState
import com.example.nexcashapplication.feature.dashboard.presentation.state.TransactionUi
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
                TransactionUi(
                    1,
                    "Kopi Kenangan",
                    35000,
                    "Makanan",
                    "Hari ini, 10:20",
                    false
                ),
                TransactionUi(
                    2,
                    "Gaji Bulanan",
                    12500000,
                    "Pekerjaan",
                    "Kemarin, 09:00",
                    true
                ),
                TransactionUi(
                    3,
                    "Grab Ride",
                    22000,
                    "Transportasi",
                    "23 Okt, 08:15",
                    false
                )
            )
    }
}