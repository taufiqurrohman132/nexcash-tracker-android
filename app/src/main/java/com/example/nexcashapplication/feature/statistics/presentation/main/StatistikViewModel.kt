package com.example.nexcashapplication.feature.statistics.presentation.main

import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.common.toEndOfMonthMillis
import com.example.nexcashapplication.core.common.toStartOfMonthMillis
import com.example.nexcashapplication.feature.statistics.domain.model.BudgetHealth
import com.example.nexcashapplication.feature.statistics.domain.model.ExpenseSummary
import com.example.nexcashapplication.feature.statistics.domain.model.SavingsRate
import com.example.nexcashapplication.feature.statistics.domain.usecase.GetBudgetHealthUseCase
import com.example.nexcashapplication.feature.statistics.domain.usecase.GetCategoryAllocationUseCase
import com.example.nexcashapplication.feature.statistics.domain.usecase.GetExpenseSummaryUseCase
import com.example.nexcashapplication.feature.statistics.domain.usecase.GetSavingsRateUseCase
import com.example.nexcashapplication.feature.statistics.presentation.main.model.AlokasiKategoriUi
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

/**
 * Statistik bulanan: alokasi donut (budget/realisasi), total pengeluaran +
 * tren, sisa anggaran, dan tingkat tabungan. Semua data ikut [selectedPeriod]
 * & [alokasiMode] lewat `flatMapLatest` — ganti bulan/tab langsung reload.
 *
 * State `sisaAnggaran`/`statusAnggaran`/`tingkatTabungan`/`tabunganTrendPercent`/
 * `totalAlokasi` dulunya tidak pernah diisi (kartu selalu angka default).
 */
@HiltViewModel
class StatistikViewModel @Inject constructor(
    private val getCategoryAllocationUseCase: GetCategoryAllocationUseCase,
    private val getExpenseSummaryUseCase: GetExpenseSummaryUseCase,
    private val getSavingsRateUseCase: GetSavingsRateUseCase,
    private val getBudgetHealthUseCase: GetBudgetHealthUseCase,
) : BaseViewModel<StatistikState, StatistikEvent, StatistikEffect>(
    StatistikState(),
) {
    private val selectedPeriod = MutableStateFlow(YearMonth.now())
    private val alokasiMode = MutableStateFlow(AlokasiMode.ALOKASI)
    private val realisasiTipe = MutableStateFlow(RealisasiTipe.PENGELUARAN)

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

            is StatistikEvent.OnRealisasiTipeChange -> {
                realisasiTipe.value = event.tipe

                updateState {
                    it.copy(
                        realisasiTipe = event.tipe,
                    )
                }
            }

            StatistikEvent.OnGantiBulanClick -> {
                updateState {
                    it.copy(
                        showMonthPicker = true,
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
            realisasiTipe,
        ) { yearMonth, mode, tipe -> Triple(yearMonth, mode, tipe) }
                .flatMapLatest { (yearMonth, mode, tipe) ->
                    combine(
                        getCategoryAllocationUseCase(
                            yearMonth.toStartOfMonthMillis(),
                            yearMonth.toEndOfMonthMillis(),
                            mode,
                            tipe,
                        ),
                        getExpenseSummaryUseCase(yearMonth),
                        getSavingsRateUseCase(yearMonth),
                        getBudgetHealthUseCase(yearMonth),
                    ) { alokasi, expense, savings, budget ->
                        StatisticsData(alokasi, expense, savings, budget)
                    }
                }
                .onEach { data ->
                    updateState {
                        it.copy(
                            alokasiDana = data.alokasi,
                            totalAlokasi = data.alokasi.sumOf { item -> item.nominal },
                            totalPengeluaran = data.expense.total,
                            pengeluaranTrendPercent = data.expense.percentChangeFromLastMonth,
                            tingkatTabungan = data.savings.rate,
                            tabunganTrendPercent = data.savings.trendPercent,
                            sisaAnggaran = if (data.budget.totalBudget > 0L) {
                                data.budget.sisa
                            } else {
                                0L
                            },
                            statusAnggaran = data.budget.toStatus(),
                        )
                    }
                }
                .launchIn(viewModelScope)
    }

    /**
     * OVER saat sisa < 0; WASPADA saat sisa tinggal < 20% total (terpakai > 80%);
     * bulan tanpa budget → AMAN netral supaya tidak menampilkan OVER palsu.
     */
    private fun BudgetHealth.toStatus(): BudgetStatus = when {
        totalBudget <= 0L -> BudgetStatus.AMAN
        sisa < 0L -> BudgetStatus.OVER
        usedPercent > 80f -> BudgetStatus.WASPADA
        else -> BudgetStatus.AMAN
    }
}

private data class StatisticsData(
    val alokasi: List<AlokasiKategoriUi>,
    val expense: ExpenseSummary,
    val savings: SavingsRate,
    val budget: BudgetHealth,
)
