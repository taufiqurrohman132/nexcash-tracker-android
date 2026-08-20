package com.example.nexcashapplication.feature.transaction.presentation.main

import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.feature.transaction.domain.usecase.GetAllCategoryUsecase
import com.example.nexcashapplication.feature.transaction.domain.usecase.GetMonthlySummaryUseCase
import com.example.nexcashapplication.feature.transaction.domain.usecase.GetTransactionsWithDetailsByMonthUseCase
import com.example.nexcashapplication.feature.transaction.presentation.main.mapper.toItemUiModelList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.time.Clock

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val getTransactionsWithDetailsByMonthUseCase: GetTransactionsWithDetailsByMonthUseCase,
    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase,
    private val getAllCategoryUsecase: GetAllCategoryUsecase,
) : BaseViewModel<TransactionState, TransactionEvent, TransactionEffect>(
    TransactionState(),
) {

    // State untuk bulan yang sedang dipilih
    private val _selectedYearMonth = MutableStateFlow(
        Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .let { "%04d-%02d".format(it.year, it.monthNumber) },
    )

    init {
        observerData()
    }

    override fun onEvent(event: TransactionEvent) {
        when (event) {
            is TransactionEvent.OnRefresh -> {

            }

            is TransactionEvent.OnTransactionClick -> {
                viewModelScope.launch {
                    if (event.transactionId == null) {
                        sendEffect(TransactionEffect.ShowSnackbar("Transaksi tidak tersedia"))
                    } else
                        sendEffect(TransactionEffect.NavigateToDetail(event.transactionId))
                }
            }

            is TransactionEvent.OnSearchClear -> {
                updateState {
                    it.copy(
                        searchQuery = "",
                    )
                }
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
                updateState {
                    it.copy(
                        searchQuery = event.query,
                    )
                }
            }

            is TransactionEvent.OnAddTransactionClick -> {
                viewModelScope.launch {
                    sendEffect(TransactionEffect.NavigateToAddTransaction)
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observerData() {
        _selectedYearMonth.flatMapLatest { yearMonth ->
            combine(
                getTransactionsWithDetailsByMonthUseCase(yearMonth = yearMonth),
                getMonthlySummaryUseCase(yearMonth = yearMonth),
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

            }
        }
                .onEach { /* update state */ }
                .launchIn(viewModelScope)

    }
}
