package com.example.nexcashapplication.feature.transaction.presentation.detail

import com.example.nexcashapplication.core.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailTransactionViewModel @Inject constructor() :
    BaseViewModel<DetailTransactionState, DetailTransactionEvent, DetailTransactionEffect>(DetailTransactionState()) {

    override fun onEvent(event: DetailTransactionEvent) {
        when (event) {
            DetailTransactionEvent.DeleteCancelled -> TODO()
            DetailTransactionEvent.DeleteClicked -> TODO()
            DetailTransactionEvent.DeleteConfirmed -> TODO()
            DetailTransactionEvent.DuplicateClicked -> TODO()
            DetailTransactionEvent.EditClicked -> TODO()
            is DetailTransactionEvent.LoadTransaction -> TODO()
            DetailTransactionEvent.ShareClicked -> TODO()
        }
    }
}
