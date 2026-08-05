package com.example.nexcashapplication.feature.transaction.presentation.detail

import com.example.nexcashapplication.core.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailTransactionViewModel @Inject constructor() : 
    BaseViewModel<DetailTransactionState, DetailTransactionEvent, Unit>(DetailTransactionState()) {

    override fun onEvent(event: DetailTransactionEvent) {
        when (event) {
            DetailTransactionEvent.OnBackClick -> {
                // TODO
            }
            DetailTransactionEvent.OnDeleteClick -> {
                // TODO
            }
            DetailTransactionEvent.OnEditClick -> {
                // TODO
            }
            DetailTransactionEvent.OnRefresh -> {
                // TODO
            }
        }
    }
}
