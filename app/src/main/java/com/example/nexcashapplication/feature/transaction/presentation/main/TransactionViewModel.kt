package com.example.nexcashapplication.feature.transaction.presentation.main

import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.feature.transaction.domain.usecase.GetAllCategoryUsecase
import com.example.nexcashapplication.feature.transaction.domain.usecase.GetMonthlySummaryUseCase
import com.example.nexcashapplication.feature.transaction.domain.usecase.GetTransactionsWithDetailsByMonthUseCase
import com.example.nexcashapplication.feature.transaction.presentation.main.mapper.toItemUiModelList
import com.example.nexcashapplication.feature.transaction.presentation.navigation.TransactionNavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val getTransactionsWithDetailsByMonthUseCase: GetTransactionsWithDetailsByMonthUseCase,
    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase,
    private val getAllCategoryUsecase: GetAllCategoryUsecase,
) : BaseViewModel<TransactionState, TransactionEvent, TransactionNavigationEvent>(
        TransactionState()
) {

    init {
        observerData()
    }

    override fun onEvent(event: TransactionEvent) {
        when (event) {
            is TransactionEvent.OnRefresh -> {

            }

            is TransactionEvent.OnTransactionClick -> {

            }

            is TransactionEvent.OnSearchClear -> {

            }

            is TransactionEvent.OnGroupToggle -> {
                val current = state.value.expandedGroups
                updateState {
                    it.copy(
                        expandedGroups = if (current.contains(event.groupLabel)) {
                            current - event.groupLabel
                        } else {
                            current + event.groupLabel
                        },
                    )
                }
            }

            is TransactionEvent.OnCategorySelected -> {
                updateState {
                    it.copy(
                        selectedCategory = event.category,
                    )
                }
            }

            is TransactionEvent.OnSearchQueryChange -> {

            }

            is TransactionEvent.OnAddTransactionClick -> {
                viewModelScope.launch {
                    sendEffect(TransactionNavigationEvent.NavigateToAddTransaction)
                }
            }
        }
    }

    private fun observerData() {
        combine(
            getTransactionsWithDetailsByMonthUseCase(yearMonth = "2023-10"),
            getMonthlySummaryUseCase(yearMonth = "2023-10"),
            getAllCategoryUsecase(),
        ) { transactionWithDetails, summary, category ->


            updateState { transactionState ->
                transactionState.copy(
                    todayTotal = summary.totalSaldo,
                    weekTotal = summary.totalSaldo,
                    remainingBudget = summary.totalSaldo,
                    caategoryList = category.map { it?.name ?: "Semua" },
                    groupedTransactions = transactionWithDetails.toItemUiModelList(),
                )
            }

        }.launchIn(viewModelScope)

    }
}
