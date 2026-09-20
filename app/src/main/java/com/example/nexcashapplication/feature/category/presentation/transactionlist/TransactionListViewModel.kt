package com.example.nexcashapplication.feature.category.presentation.transactionlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.transaction.presentation.main.mapper.toItemUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<TransactionListState, TransactionListEvent, TransactionListEffect>(initialState = TransactionListState()) {

    private val route: Route.CategoryRoute.SubCategoryList = savedStateHandle.toRoute()
    private val categoryId = route.categoryId

    init {
        observeTransactions()
    }

    override fun onEvent(event: TransactionListEvent) {
        when (event) {
            is TransactionListEvent.OnBackClick -> viewModelScope.launch {
                sendEffect(TransactionListEffect.NavigateBack)
            }

            is TransactionListEvent.OnTransactionClick -> viewModelScope.launch {
                sendEffect(
                    TransactionListEffect.NavigateToTransactionDetail(event.transactionId),
                )
            }

            is TransactionListEvent.OnRetryClick -> {

            }
        }
    }

    private fun observeTransactions() {
        transactionRepository.getRecentTransactionsByCategory(-1 , categoryId)
                .onEach { listTransaction ->
                    updateState {
                        it.copy(
                            transactions = listTransaction.map { transaction -> transaction.toItemUiModel() }
                        )
                    }
                }
                .launchIn(viewModelScope)
    }
}
