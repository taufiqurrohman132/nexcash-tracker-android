package com.example.nexcashapplication.feature.transaction.presentation.main

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.core.i18n.uiText
import com.example.nexcashapplication.feature.category.presentation.mapper.toCategoryNameItemUiModel
import com.example.nexcashapplication.feature.transaction.domain.usecase.GetAllCategoryUsecase
import com.example.nexcashapplication.feature.transaction.domain.usecase.GetMonthlySummaryUseCase
import com.example.nexcashapplication.feature.transaction.domain.usecase.GetTransactionsWithDetailsByMonthUseCase
import com.example.nexcashapplication.feature.transaction.presentation.main.mapper.toItemUiModel
import com.example.nexcashapplication.feature.transaction.presentation.main.mapper.toItemUiModelList
import com.example.nexcashapplication.feature.transaction.presentation.main.model.CategoryNameItemUiModel
import com.example.nexcashapplication.feature.transaction.presentation.main.model.TransactionUiGroupModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

private const val TAG = "TransactionVm"

/**
 * Data terakhir yang tampil, disimpan lintas ViewModel.
 *
 * Pindah tab memakai `saveState`/`restoreState` (lihat BottomNavBar), sehingga
 * NavBackStackEntry lama dibuang dan ViewModel dibuat ulang tiap kembali ke tab
 * ini. Dengan cache ini frame pertama sudah terisi data lama, lalu flow Room
 * menimpanya dengan data segar — tidak ada layar kosong + query dari nol.
 */
private data class TransactionSnapshot(
    val month: YearMonth,
    val categoryId: String?,
    val groupedTransactions: List<TransactionUiGroupModel>,
    val categoryList: List<CategoryNameItemUiModel?>,
)

private var lastSnapshot: TransactionSnapshot? = null

/** Hasil satu putaran `observerData` sebelum masuk ke state. */
private data class LoadedData(
    val month: String,
    val totals: Long,
    val grouped: List<TransactionUiGroupModel>,
    val categoryList: List<CategoryNameItemUiModel?>,
)

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val getTransactionsWithDetailsByMonthUseCase: GetTransactionsWithDetailsByMonthUseCase,
    private val getMonthlySummaryUseCase: GetMonthlySummaryUseCase,
    private val getAllCategoryUsecase: GetAllCategoryUsecase,
    private val transactionRepository: TransactionRepository,
) : BaseViewModel<TransactionState, TransactionEvent, TransactionEffect>(
    TransactionState(),
) {

    // Bulan yang sedang dipilih: sumbernya satu — TransactionState.selectedMonth.
    // Dipetakan ke "yyyy-MM" karena format itu yang diminta use case.
    private val selectedMonth: Flow<String> = state
            .map { it.selectedMonth.toString() }
            .distinctUntilChanged()

    private val _selectedCategoryId = MutableStateFlow<String?>(null)

    // Fase 0 (diagnostik): kapan VM dibuat & kapan data pertama sampai.
    // Kalau "VM dibuat" muncul tiap pindah tab, berarti VM memang dibuat ulang
    // dan cache snapshot di bawah yang bikin load kedua terasa instan.
    private val createdAt = SystemClock.uptimeMillis()
    private var firstDataLogged = false

    init {
        Log.d(TAG, "VM dibuat ulang (bulan=${state.value.selectedMonth})")
        restoreSnapshot()
        observerData()
        observerSearch()
    }

    /** Suntik data lama biar frame pertama tidak kosong — lihat [lastSnapshot]. */
    private fun restoreSnapshot() {
        val cached = lastSnapshot ?: return
        if (cached.month != state.value.selectedMonth) return

        // filter kategori ikut dipulihkan supaya chip dan daftar tidak beda
        _selectedCategoryId.value = cached.categoryId
        updateState {
            it.copy(
                selectedCategoryId = cached.categoryId,
                groupedTransactions = cached.groupedTransactions,
                categoryList = cached.categoryList,
                isLoading = false,
            )
        }
        Log.d(TAG, "snapshot dipakai → frame pertama langsung terisi")
    }

    override fun onEvent(event: TransactionEvent) {
        when (event) {
            is TransactionEvent.OnRefresh -> {

            }

            is TransactionEvent.OnTransactionClick -> {
                viewModelScope.launch {
                    if (event.transactionId == null) {
                        sendEffect(
                            TransactionEffect.ShowSnackbar(
                                uiText(R.string.tx_transaction_unavailable),
                            ),
                        )
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
                        isLoading = true,
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
                        isLoading = true,
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
                // grouping + format per baris itu kerja berat; dipisah dari
                // updateState supaya bisa digeser ke background oleh flowOn
                LoadedData(
                    month = yearMonth,
                    totals = summary.totalSaldo,
                    grouped = transactionWithDetails.toItemUiModelList(),
                    categoryList = categories.map { it?.toCategoryNameItemUiModel() },
                )
            }
                    // Room bisa mengirim ulang hasil yang sama saat ada invalidasi
                    // tabel terkait (relasi @Relation) — buang duplikatnya supaya
                    // state tidak menyalin list baru tanpa perubahan
                    .distinctUntilChanged()
                    // flowOn mencakup seluruh upstream, jadi transform di atas
                    // ikut pindah ke thread background — main thread bebas
                    // selama animasi transisi pindah tab
                    .flowOn(Dispatchers.Default)
        }
                // dari sini ke bawah kembali ke konteks viewModelScope (Main)
                .onEach { data ->
                    if (!firstDataLogged) {
                        firstDataLogged = true
                        Log.d(
                            TAG,
                            "data pertama bulan=${data.month} setelah " +
                                    "${SystemClock.uptimeMillis() - createdAt} ms",
                        )
                    }

                    updateState { transactionState ->
                        transactionState.copy(
                            todayTotal = data.totals,
                            weekTotal = data.totals,
                            remainingBudget = data.totals,
                            categoryList = data.categoryList,
                            groupedTransactions = data.grouped,
                            isLoading = false,
                        )
                    }

                    lastSnapshot = TransactionSnapshot(
                        month = runCatching { YearMonth.parse(data.month) }
                                .getOrDefault(state.value.selectedMonth),
                        categoryId = state.value.selectedCategoryId,
                        groupedTransactions = data.grouped,
                        categoryList = data.categoryList,
                    )
                }
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
                    // mapping hasil search juga di luar main thread
                    .flowOn(Dispatchers.Default)
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
