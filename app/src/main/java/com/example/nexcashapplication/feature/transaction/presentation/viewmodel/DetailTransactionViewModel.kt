package com.example.nexcashapplication.feature.transaction.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.domain.model.TransactionUi
import com.example.nexcashapplication.feature.transaction.presentation.event.TransactionEvent
import com.example.nexcashapplication.feature.transaction.presentation.state.TransactionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DetailTransactionViewModel : ViewModel() {
    private var _state = MutableStateFlow(
        TransactionState(
            searchQuery = "Makan",
            caategoryList = listOf(
                "Semua",
                "Food",
                "Transport",
                "Shopping",
                "Salary"
            ),
            selectedCategory = "Semua",

            todayTotal = 150_000,
            weekTotal = 1_250_000,
            remainingBudget = 3_750_000,

            groupedTransactions = mapOf(
                "Hari Ini" to listOf(
                    TransactionUi(
                        id = "1",
                        title = "Makan Siang",
                        subtitle = "GoPay",
                        time = "12:30",
                        amount = 35_000,
                        isIncome = false,
                        category = "Food",
                        iconRes = R.drawable.eye_show
                    ),
                    TransactionUi(
                        id = "2",
                        title = "Isi Bensin",
                        subtitle = "OVO",
                        time = "17:15",
                        amount = 50_000,
                        isIncome = false,
                        category = "Transport",
                        iconRes = R.drawable.transfer
                    )
                ),

                "Kemarin" to listOf(
                    TransactionUi(
                        id = "3",
                        title = "Gaji Bulanan",
                        subtitle = "BCA",
                        time = "08:00",
                        amount = 5_000_000,
                        isIncome = true,
                        category = "Salary",
                        iconRes = R.drawable.transaction
                    ),
                    TransactionUi(
                        id = "4",
                        title = "Belanja Bulanan",
                        subtitle = "ShopeePay",
                        time = "19:20",
                        amount = 250_000,
                        isIncome = false,
                        category = "Shopping",
                        iconRes = R.drawable.scan
                    )
                )
            ),

            expandedGroups = setOf(
                "Hari Ini",
                "Kemarin"
            ),

            isLoading = false,
            errorMessage = null
        )
    )

    val state: StateFlow<TransactionState> =
        _state.asStateFlow()

    fun onEvent(event: TransactionEvent) {
        when (event) {
            is TransactionEvent.OnRefresh -> {

            }

            is TransactionEvent.OnTransactionClick -> {

            }

            is TransactionEvent.OnSearchClear -> {

            }

            is TransactionEvent.OnGroupToggle -> {
                val current = state.value.expandedGroups
                _state.update {
                    it.copy(
                        expandedGroups = if (current.contains(event.groupLabel)) {
                            current - event.groupLabel
                        } else {
                            current + event.groupLabel
                        }
                    )
                }
            }

            is TransactionEvent.OnCategorySelected -> {
                _state.update {
                    it.copy(
                        selectedCategory = event.category
                    )
                }
            }

            is TransactionEvent.OnSearchQueryChange -> {

            }

            is TransactionEvent.OnAddTransactionClick -> {

            }
        }
    }
}