package com.example.nexcashapplication.feature.transaction.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.transaction.presentation.detail.mapper.toDeleteDialogUiState
import com.example.nexcashapplication.feature.transaction.presentation.detail.mapper.toDetailTransactionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<DetailTransactionState, DetailTransactionEvent, DetailTransactionEffect>(
    DetailTransactionState(),
) {

    private val route: Route.TransactionRoute.Detail = savedStateHandle.toRoute()
    private val transactionId = route.transactionId

    init {
        loadTransaction(transactionId)
    }

    override fun onEvent(event: DetailTransactionEvent) {
        when (event) {
            DetailTransactionEvent.OnAttachmentClick -> TODO()
            DetailTransactionEvent.OnBackClick -> {
                viewModelScope.launch {
                    sendEffect(DetailTransactionEffect.NavigateBack)
                }
            }

            DetailTransactionEvent.OnDeleteClick -> {
                updateState {
                    it.copy(
                        deleteDialogState = it.transaction?.toDeleteDialogUiState(),
                    )
                }
            }

            is DetailTransactionEvent.OnDeleteConfirm -> {
                handleDeleteTransaction(transactionId)
            }

            DetailTransactionEvent.OnDismissDeleteDialog -> {
                updateState {
                    it.copy(
                        deleteDialogState = null,
                    )
                }
            }
            DetailTransactionEvent.OnDismissMenu -> TODO()
            DetailTransactionEvent.OnDuplicateClick -> TODO()
            DetailTransactionEvent.OnEditClick -> {
                viewModelScope.launch {
                    sendEffect(DetailTransactionEffect.NavigateToEdit(transactionId))
                }
            }

            DetailTransactionEvent.OnMenuClick -> TODO()
            DetailTransactionEvent.OnShareClick -> TODO()
        }
    }

    fun loadTransaction(id: String) {
        transactionRepository.getTransactionWithDetailsById(id)
                .onEach { transactionWithDetails ->
                    if (transactionWithDetails == null) {
                        // Transaksi udah kehapus — kemungkinan besar karena delete sukses,
                        // atau race condition lain. Navigate back, bukan crash atau nampilin state kosong.
                        sendEffect(DetailTransactionEffect.NavigateBack)
                        return@onEach
                    }
                    updateState {
                        it.copy(
                            isLoading = false,
                            transaction = transactionWithDetails.toDetailTransactionUiModel(),
                        )
                    }
                }
                .launchIn(viewModelScope)
    }


    private fun handleDeleteTransaction(transactionId: String) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transactionId)
            sendEffect(DetailTransactionEffect.NavigateBack)
        }
    }
}
