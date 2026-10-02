package com.example.nexcashapplication.feature.statistics.presentation.main

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.common.toEndOfMonthMillis
import com.example.nexcashapplication.core.common.toStartOfMonthMillis
import com.example.nexcashapplication.core.domain.repository.BudgetRepository
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.statistics.domain.model.BudgetHealth
import com.example.nexcashapplication.feature.statistics.domain.model.ExpenseSummary
import com.example.nexcashapplication.feature.statistics.domain.model.SavingsRate
import com.example.nexcashapplication.feature.statistics.domain.usecase.GetBudgetHealthUseCase
import com.example.nexcashapplication.feature.statistics.domain.usecase.GetCategoryAllocationUseCase
import com.example.nexcashapplication.feature.statistics.domain.usecase.GetExpenseSummaryUseCase
import com.example.nexcashapplication.feature.statistics.domain.usecase.GetSavingsRateUseCase
import com.example.nexcashapplication.feature.statistics.presentation.main.model.AlokasiKategoriUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

private const val TAG = "StatistikVm"

/**
 * Data terakhir yang tampil, disimpan lintas ViewModel.
 *
 * Sama seperti snapshot di TransactionViewModel: `saveState`/`restoreState`
 * di BottomNavBar membuat ViewModel baru tiap kembali ke tab ini, jadi tanpa
 * cache ini frame pertama selalu menampilkan kartu Rp 0 + donut kosong sambil
 * nunggu 8 query.
 */

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
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
) : BaseViewModel<StatistikState, StatistikEvent, StatistikEffect>(
    StatistikState(),
) {
    private val selectedPeriod = MutableStateFlow(YearMonth.now())
    private val alokasiMode = MutableStateFlow(AlokasiMode.ALOKASI)
    private val realisasiTipe = MutableStateFlow(RealisasiTipe.PENGELUARAN)

    // Fase 0 (diagnostik): kapan VM dibuat & kapan data pertama sampai —
    // tag StatistikVm supaya bisa disaring: adb logcat -s StatistikVm
    private val createdAt = SystemClock.uptimeMillis()
    private var firstDataLogged = false

    init {
        Log.d(TAG, "VM dibuat ulang (bulan=${state.value.selectedMonth})")
        restoreSnapshot()
        observeStatistics()
    }

    /**
     * Suntik data lama biar frame pertama tidak kartu Rp 0 — dan sinkronkan
     * juga flow bulan/tab/mode supaya query pertama sudah memakai bulan yang
     * sama dengan yang tampil (kalau tidak, ada satu putaran query bulan default
     * yang langsung dibuang).
     */
    private fun restoreSnapshot() {
        val cached = lastSnapshot ?: return

        selectedPeriod.value = cached.selectedMonth
        alokasiMode.value = cached.alokasiMode
        realisasiTipe.value = cached.realisasiTipe
        updateState {
            if (cached.alokasiDana.isEmpty()) {
                // belum pernah ada data → tetap tampilkan indikator loading
                StatistikState(
                    selectedMonth = cached.selectedMonth,
                    alokasiMode = cached.alokasiMode,
                    realisasiTipe = cached.realisasiTipe,
                    isLoading = true,
                )
            } else {
                cached.copy(isLoading = false)
            }
        }
        Log.d(TAG, "snapshot dipakai → frame pertama langsung terisi")
    }

    override fun onEvent(event: StatistikEvent) {
        when (event) {
            is StatistikEvent.OnAlokasiTabChange -> {
                alokasiMode.value = event.mode

                updateState {
                    it.copy(
                        alokasiMode = event.mode,
                        // ganti tab alokasi = query baru (budget ↔ aktual)
                        isLoading = true,
                    )
                }
            }

            is StatistikEvent.OnRealisasiTipeChange -> {
                realisasiTipe.value = event.tipe

                updateState {
                    it.copy(
                        realisasiTipe = event.tipe,
                        isLoading = true,
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
                        isLoading = true,
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
                    val monthStart = yearMonth.toStartOfMonthMillis()
                    val monthEnd = yearMonth.toEndOfMonthMillis()

                    // Dua query dipakai oleh dua use case (budget & total
                    // pengeluaran) — dibagikan lewat stateIn supaya Room hanya
                    // menjalankannya sekali, bukan dua kali per emisi.
                    // WhileSubscribed: begitu ganti bulan/mode, collector lama
                    // berhenti dan query ikut mati.
                    val budgets = budgetRepository.getBudgetsByMonth(monthStart, monthEnd)
                            .stateIn(
                                viewModelScope,
                                SharingStarted.WhileSubscribed(SHARE_TIMEOUT_MS),
                                emptyList(),
                            )
                    val expenseThisMonth = transactionRepository
                            .getTotalExpenseByMonth(monthStart, monthEnd)
                            .stateIn(
                                viewModelScope,
                                SharingStarted.WhileSubscribed(SHARE_TIMEOUT_MS),
                                0L,
                            )

                    combine(
                        getCategoryAllocationUseCase(
                            monthStart,
                            monthEnd,
                            mode,
                            tipe,
                            budgetsFlow = budgets,
                        ),
                        getExpenseSummaryUseCase(yearMonth, currentTotalFlow = expenseThisMonth),
                        getSavingsRateUseCase(yearMonth),
                        getBudgetHealthUseCase(
                            yearMonth,
                            budgetsFlow = budgets,
                            expenseFlow = expenseThisMonth,
                        ),
                    ) { alokasi, expense, savings, budget ->
                        StatisticsData(alokasi, expense, savings, budget)
                    }
                }
                // transform `combine` di atas walau ringan ikut pindah ke
                // background supaya main thread bebas selama animasi pindah tab
                .flowOn(Dispatchers.Default)
                .onEach { data ->
                    if (!firstDataLogged) {
                        firstDataLogged = true
                        Log.d(
                            TAG,
                            "data pertama bulan=${state.value.selectedMonth} setelah " +
                                    "${SystemClock.uptimeMillis() - createdAt} ms",
                        )
                    }

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
                            isLoading = false,
                        )
                    }
                    lastSnapshot = state.value
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

    companion object {
        /**
         * Data terakhir yang tampil, disimpan lintas ViewModel (statik di level
         * proses) — test wajib meresetnya antar test:
         * `StatistikViewModel.lastSnapshot = null`.
         */
        internal var lastSnapshot: StatistikState? = null

        /** Lihat `SharingStarted.WhileSubscribed` di [observeStatistics]. */
        private const val SHARE_TIMEOUT_MS = 1_000L
    }
}

private data class StatisticsData(
    val alokasi: List<AlokasiKategoriUi>,
    val expense: ExpenseSummary,
    val savings: SavingsRate,
    val budget: BudgetHealth,
)
