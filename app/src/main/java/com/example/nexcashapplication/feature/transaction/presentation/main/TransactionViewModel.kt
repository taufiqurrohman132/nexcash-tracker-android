package com.example.nexcashapplication.feature.transaction.presentation.main

import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.category.presentation.mapper.toCategoryNameItemUiModel
import com.example.nexcashapplication.feature.transaction.domain.usecase.GetAllCategoryUsecase
import com.example.nexcashapplication.feature.transaction.domain.usecase.GetMonthlySummaryUseCase
import com.example.nexcashapplication.feature.transaction.domain.usecase.GetTransactionsWithDetailsByMonthUseCase
import com.example.nexcashapplication.feature.transaction.presentation.main.mapper.toItemUiModel
import com.example.nexcashapplication.feature.transaction.presentation.main.mapper.toItemUiModelList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val getTransactionsWithDetailsByMonthUseCase: GetTransactionsWithDetailsByMonthUseCase,
    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase,
    private val getAllCategoryUsecase: GetAllCategoryUsecase,
    private val transactionRepository: TransactionRepository,
) : BaseViewModel<TransactionState, TransactionEvent, TransactionEffect>(
    TransactionState(),
) {

    // Bulan yang sedang dipilih: sumbernya satu — TransactionState.selectedMonth
    // (diisi lewat navigator bulan di TopAppBar). Dipetakan ke "yyyy-MM" karena
    // format itu yang diminta use case.
    private val selectedMonth: Flow<String> = state
            .map { it.selectedMonth.toString() }
            .distinctUntilChanged()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)

    init {
        observerData()
        observerSearch()
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
                val current = state.value.collapsGroups
                updateState {
                    it.copy(
                        collapsGroups = if (current.contains(event.groupLabel)) {
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
                        selectedCategoryId = event.categoryId,
                    )
                }
                _selectedCategoryId.value = event.categoryId
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

            is TransactionEvent.OnMonthSelected -> {
                // sekalian menutup picker kalau datang dari konfirmasi MonthPicker
                updateState {
                    it.copy(
                        selectedMonth = event.month,
                        showMonthPicker = false,
                    )
                }
            }

            is TransactionEvent.OnOpenMonthPicker -> {
                updateState { it.copy(showMonthPicker = true) }
            }

            is TransactionEvent.OnMonthPickerDismiss -> {
                updateState { it.copy(showMonthPicker = false) }
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observerData() {

        combine(
            selectedMonth,
            _selectedCategoryId,
        ) { yearMonth, categoryId ->
            yearMonth to categoryId
        }.flatMapLatest { (yearMonth, categorySelectedId) ->
            combine(
                getTransactionsWithDetailsByMonthUseCase(
                    yearMonth = yearMonth,
                    categoryFilterId = categorySelectedId,
                ),
                getMonthlySummaryUseCase(yearMonth = yearMonth),
                getAllCategoryUsecase(),
            ) { transactionWithDetails, summary, categories ->


                updateState { transactionState ->
                    transactionState.copy(
                        todayTotal = summary.totalSaldo,
                        weekTotal = summary.totalSaldo,
                        remainingBudget = summary.totalSaldo,
                        categoryList = categories.map { it?.toCategoryNameItemUiModel() },
                        groupedTransactions = transactionWithDetails.toItemUiModelList(),
                    )
                }

            }
        }
                .onEach { /* update state */ }
                .launchIn(viewModelScope)

    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun observerSearch() {
        viewModelScope.launch {
            state.map { it.searchQuery }
                    .distinctUntilChanged()
                    .debounce(300.milliseconds)
                    .flatMapLatest { query ->
                        if (query.isBlank()) {
                            flowOf(emptyList())
                        } else {
                            transactionRepository.searchTransactions(query)
                        }
                    }
                    .map { list -> list.map { it.toItemUiModel() } }
                    .collect { result ->
                        updateState {
                            it.copy(
                                trasactionsSearchList = result,
                            )
                        }
                    }
        }
    }
}
