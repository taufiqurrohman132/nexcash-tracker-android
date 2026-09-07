package com.example.nexcashapplication.feature.statistics.presentation.main

import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.common.toEndOfMonthMillis
import com.example.nexcashapplication.core.common.toStartOfMonthMillis
import com.example.nexcashapplication.feature.statistics.domain.usecase.GetCategoryAllocationUseCase
import com.example.nexcashapplication.feature.statistics.domain.usecase.GetExpenseSummaryUseCase
import com.example.nexcashapplication.feature.statistics.presentation.main.StatistikEffect.NavigateToTargetDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class StatistikViewModel @Inject constructor(
    private val getCategoryAllocationUseCase: GetCategoryAllocationUseCase,
    private val getExpenseSummaryUseCase: GetExpenseSummaryUseCase,
) : BaseViewModel<StatistikState, StatistikEvent, StatistikEffect>(
    StatistikState(),
) {
    private val selectedPeriod = MutableStateFlow(YearMonth.now())
    private val alokasiMode = MutableStateFlow(AlokasiMode.ALOKASI)

    init {
        observeStatistics()
    }

    override fun onEvent(event: StatistikEvent) {
        when (event) {
            is StatistikEvent.OnAlokasiTabChange -> {
                alokasiMode.value = event.mode

                updateState {
                    it.copy(
                        alokasiMode = event.mode,
                    )
                }
            }

            StatistikEvent.OnGantiBulanClick -> {
                updateState {
                    it.copy(
                        showMonthPicker = true,
                    )
                }
//                viewModelScope.launch {
//                    sendEffect(
//                        ShowMonthPicker(
//                            current = state.value.selectedMonth,
//                        ),
//                    )
//                }
            }

            StatistikEvent.OnLihatSemuaTargetClick -> {
                viewModelScope.launch {
                    sendEffect(StatistikEffect.NavigateToTargetList)
                }
            }

            StatistikEvent.OnModeRencanaToggle -> {
                updateState {
                    it.copy(
                        isModeRencanaActive = !it.isModeRencanaActive,
                    )
                }
            }

            is StatistikEvent.OnMonthSelected -> {
                selectedPeriod.value = event.month

                updateState {
                    it.copy(
                        selectedMonth = event.month,
                        showMonthPicker = false,
                    )
                }
            }

            is StatistikEvent.OnTargetClick -> {
                viewModelScope.launch {
                    sendEffect(NavigateToTargetDetail(event.id))
                }
            }

            StatistikEvent.OnUbahAlokasiClick -> {
                viewModelScope.launch {
                    sendEffect(StatistikEffect.NavigateToUbahAlokasi)
                }
            }

            StatistikEvent.OnMonthPickerDismiss -> {
                updateState {
                    it.copy(
                        showMonthPicker = false,
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeStatistics() {
        combine(
            selectedPeriod,
            alokasiMode,
        ) { yearMonth, alokasiMode ->
            Pair(yearMonth, alokasiMode)
        }
                .flatMapLatest { (yearMonth, alokasiMode) ->
                    combine(
                        getCategoryAllocationUseCase(
                            yearMonth.toStartOfMonthMillis(),
                            yearMonth.toEndOfMonthMillis(),
                            alokasiMode,
                        ),
                        getExpenseSummaryUseCase(
                            yearMonth.toStartOfMonthMillis(), yearMonth.toEndOfMonthMillis(),
                        ),
                    ) { listAlokasi, expenseSummary ->
                        Triple(listAlokasi, expenseSummary, yearMonth)
                    }
                }
                .onEach { (listAlokasi, expenseSummary, _) ->
                    updateState {
                        it.copy(
                            alokasiDana = listAlokasi,
                            totalPengeluaran = expenseSummary.total,
                            pengeluaranTrendPercent = expenseSummary.percentChangeFromLastMonth,
//                            selectedMonth = yearMonth,
                        )
                    }
                }
                .launchIn(viewModelScope)
    }
}