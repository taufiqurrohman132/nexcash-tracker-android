package com.example.nexcashapplication.feature.dashboard.presentation.ui

import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.dashboard.presentation.event.DashboardEvent
import com.example.nexcashapplication.feature.dashboard.presentation.state.DashboardState
import com.example.nexcashapplication.feature.transaction.presentation.main.mapper.toItemUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    init {
        loadData()
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
        recentTransactionsJob = transactionRepository
                .getRecentTransactions(limit = RECENT_LIMIT)
                .onEach { transactionsWithDetail ->
                    updateState { dashboardState ->
                        dashboardState.copy(
                            transactions = transactionsWithDetail.map { it.toItemUiModel() },
                        )
                    }
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
    }
}
