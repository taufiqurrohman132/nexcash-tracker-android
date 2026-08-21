package com.example.nexcashapplication.feature.transaction.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.transaction.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.transaction.presentation.detail.mapper.toDetailTrasactionState
import dagger.hilt.android.lifecycle.HiltViewModel
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
        loadData()
    }

    override fun onEvent(event: DetailTransactionEvent) {
        when (event) {
            DetailTransactionEvent.OnAttachmentClick -> TODO()
            DetailTransactionEvent.OnBackClick -> {
                viewModelScope.launch {
                    sendEffect(DetailTransactionEffect.NavigateBack)
                }
            }
            DetailTransactionEvent.OnDeleteClick -> TODO()
            DetailTransactionEvent.OnDeleteConfirm -> TODO()
            DetailTransactionEvent.OnDismissDeleteDialog -> TODO()
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

    private fun loadData() {
        viewModelScope.launch {
            transactionRepository.getTransactionWithDetailsById(transactionId)
                    .collect { transaction ->
                        updateState {
                            transaction.toDetailTrasactionState()
                        }
                    }
        }

    }
}
