package com.example.nexcashapplication.feature.transaction.presentation.alltransactions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionFilter
import com.example.nexcashapplication.feature.transaction.domain.usecase.GetTransactionsByFilterUseCase
import com.example.nexcashapplication.feature.transaction.presentation.main.mapper.toItemUiModelList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Satu ViewModel untuk semua titik "Lihat Semua transaksi".
 *
 * Filter dibaca sekali dari route (`Route.TransactionRoute.All`); logika per-varian
 * ada di [GetTransactionsByFilterUseCase]. Menambah varian filter baru tidak
 * menyentuh kelas ini (ADR-002).
 */
@HiltViewModel
class AllTransactionsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTransactionsByFilterUseCase: GetTransactionsByFilterUseCase,
) : BaseViewModel<AllTransactionsState, AllTransactionsEvent, AllTransactionsEffect>(
    AllTransactionsState(),
) {

    private val route: Route.TransactionRoute.All = savedStateHandle.toRoute()
    private val filter: TransactionFilter =
        TransactionFilter.fromRoute(route.categoryId, route.accountId)

    private var titleJob: Job? = null
    private var transactionsJob: Job? = null

    init {
        observeTitle()
        observeTransactions()
    }

    override fun onEvent(event: AllTransactionsEvent) {
        when (event) {
            AllTransactionsEvent.OnBackClick -> viewModelScope.launch {
                sendEffect(AllTransactionsEffect.NavigateBack)
            }

            is AllTransactionsEvent.OnTransactionClick -> viewModelScope.launch {
                sendEffect(
                    AllTransactionsEffect.NavigateToTransactionDetail(event.transactionId),
                )
            }

            AllTransactionsEvent.OnAddTransactionClick -> viewModelScope.launch {
                sendEffect(AllTransactionsEffect.NavigateToAddTransaction)
            }

            is AllTransactionsEvent.OnGroupToggle -> updateState { current ->
                current.copy(
                    collapsedGroups = if (current.collapsedGroups.contains(event.groupTitle)) {
                        current.collapsedGroups - event.groupTitle
                    } else {
                        current.collapsedGroups + event.groupTitle
                    },
                )
            }

            AllTransactionsEvent.OnRetryClick -> {
                observeTitle()
                observeTransactions()
            }
        }
    }

    private fun observeTitle() {
        titleJob?.cancel()
        titleJob = getTransactionsByFilterUseCase.topBarTitle(filter)
            .catch { /* judul gagal → pertahankan judul default */ }
            .onEach { title -> updateState { it.copy(title = title) } }
            .launchIn(viewModelScope)
    }

    private fun observeTransactions() {
        transactionsJob?.cancel()
        updateState { it.copy(isLoading = true, error = null) }

        transactionsJob = getTransactionsByFilterUseCase.transactions(filter)
            .map { transactions -> transactions.toItemUiModelList() }
            .catch { e ->
                updateState {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Gagal memuat transaksi",
                    )
                }
            }
            .onEach { groups ->
                updateState {
                    it.copy(isLoading = false, groupedTransactions = groups, error = null)
                }
            }
            .launchIn(viewModelScope)
    }
}
