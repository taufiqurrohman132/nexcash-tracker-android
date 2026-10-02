package com.example.nexcashapplication.feature.dashboard.presentation.ui

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.dashboard.presentation.event.DashboardEvent
import com.example.nexcashapplication.feature.dashboard.presentation.state.DashboardState
import com.example.nexcashapplication.feature.transaction.presentation.main.mapper.toItemUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomeVm"

/**
 * Data terakhir yang tampil, disimpan lintas ViewModel — pindah tab memakai
 * `saveState`/`restoreState` (BottomNavBar) sehingga ViewModel dibuat ulang tiap
 * kembali ke Home. Tanpa cache ini frame pertama selalu kartu transaksi kosong.
 *
 * `internal` supaya test bisa mereset cache antar test (variabelnya statis,
 * kalau tidak direset, test berikutnya langsung kebagian data test sebelumnya).
 */
internal data class DashboardSnapshot(
    val transactions: List<TransactionItemUiModel>,
)

/**
 * ViewModel dashboard.
 *
 * - "Transaksi terakhir": tanpa filter kategori (baru [getRecentTransactions]),
 *   terbaru, maks [RECENT_LIMIT].
 * - Semua event punya penanganan — `TODO()` di sini berarti crash begitu tombol ditekan.
 * - Tujuan notifikasi/profil/transfer/tagihan/scan/lainnya belum dipetakan di
 *   [DashboardScreen] (efeknya diterima lalu diabaikan `else -> Unit`), tapi VM sudah
 *   mengirim effect yang benar agar UI tinggal menyambungkan navigasinya.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
) : BaseViewModel<DashboardState, DashboardEvent, DashboardEffect>(
    DashboardState(),
) {

    /** Collector "transaksi terakhir" — dibatalkan saat reload agar tidak dobel. */
    private var recentTransactionsJob: Job? = null

    // Fase 0 (diagnostik): kapan VM dibuat & kapan data pertama sampai —
    // adb logcat -s HomeVm
    private val createdAt = SystemClock.uptimeMillis()
    private var firstDataLogged = false

    init {
        Log.d(TAG, "VM dibuat ulang")
        restoreSnapshot()
        loadData()
    }

    /** Suntik data lama biar frame pertama tidak kartu kosong. */
    private fun restoreSnapshot() {
        val cached = lastSnapshot ?: return

        updateState {
            it.copy(
                transactions = cached.transactions,
                // data kosong → tetap tampilkan indikator sampai emisi pertama
                isLoading = cached.transactions.isEmpty(),
            )
        }
        Log.d(TAG, "snapshot dipakai → frame pertama langsung terisi")
    }

    override fun onEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.OnAddTransactionClick ->
                navigate(DashboardEffect.NavigateToAddTransaction)

            DashboardEvent.OnLainnyaClick -> navigate(DashboardEffect.NavigateToMoreMenu)
            DashboardEvent.OnLihatDetailBudgetClick ->
                navigate(DashboardEffect.NavigateToBudgetDetail)

            DashboardEvent.OnNotificationClick -> navigate(DashboardEffect.NavigateToNotifications)
            DashboardEvent.OnProfileClick -> navigate(DashboardEffect.NavigateToProfile)
            DashboardEvent.OnScanClick -> navigate(DashboardEffect.NavigateToScan)
            DashboardEvent.OnSemuaTransaksiClick ->
                navigate(DashboardEffect.NavigateToAllTransactions)

            DashboardEvent.OnTagihanClick -> navigate(DashboardEffect.NavigateToTagihan)
            DashboardEvent.OnTransferClick -> navigate(DashboardEffect.NavigateToTransfer)

            DashboardEvent.OnRefresh,
            DashboardEvent.OnScreenLoad,
            -> loadData()

            DashboardEvent.OnToggleBalanceVisibility -> updateState {
                it.copy(isBalanceVisible = !it.isBalanceVisible)
            }

            is DashboardEvent.OnTransactionClick -> navigate(
                DashboardEffect.NavigateToTransactionDetail(
                    transactionId = event.transactionId,
                ),
            )
        }
    }

    fun loadData() {
        recentTransactionsJob?.cancel()
        // reload ditandai lewat isLoading (indikator hanya muncul kalau daftar
        // kosong, jadi data lama tidak berkedip) — sekaligus jadi penanda buat
        // test bahwa emisi berikutnya benar-benar sudah sampai
        updateState { it.copy(isLoading = true) }
        recentTransactionsJob = transactionRepository
                .getRecentTransactions(limit = RECENT_LIMIT)
                // mapping entity → UI (format tanggal, ambil warna kategori)
                // dipindah dari onEach main thread — ini yang bikin pindah tab
                // ke Home tersendat
                .map { list -> list.map { it.toItemUiModel() } }
                .flowOn(Dispatchers.Default)
                .onEach { transactions ->
                    if (!firstDataLogged) {
                        firstDataLogged = true
                        Log.d(
                            TAG,
                            "data pertama (${transactions.size} baris) setelah " +
                                    "${SystemClock.uptimeMillis() - createdAt} ms",
                        )
                    }

                    updateState { dashboardState ->
                        dashboardState.copy(
                            transactions = transactions,
                            isLoading = false,
                        )
                    }
                    lastSnapshot = DashboardSnapshot(transactions = transactions)
                }
                .launchIn(viewModelScope)
    }

    private fun navigate(effect: DashboardEffect) {
        viewModelScope.launch {
            sendEffect(effect)
        }
    }

    companion object {
        private const val RECENT_LIMIT = 5

        /** Lihat [DashboardSnapshot] — statis, jadi harus di-reset di antar test. */
        internal var lastSnapshot: DashboardSnapshot? = null
    }
}
