package com.example.nexcashapplication.feature.transaction.presentation.addnew

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddTransactionViewModel : ViewModel() {
    private var _state = MutableStateFlow(
        AddTransactionState(
            type = TransactionType.EXPENSE,
            amountDisplay = "Rp 0",
        )
    )

    val state: StateFlow<AddTransactionState> =
        _state.asStateFlow()

    fun onEvent(event: AddTransactionEvent) {
        when (event) {
            is AddTransactionEvent.OnAmountClick -> {

            }

            is AddTransactionEvent.OnTimeClick -> {

            }

            is AddTransactionEvent.OnSaveClick -> {

            }

            is AddTransactionEvent.OnAccountClick -> {
            }

            is AddTransactionEvent.OnCategoryClick -> {
            }

            is AddTransactionEvent.OnAttachmentClick -> {

            }

            is AddTransactionEvent.OnBackClick -> {

            }

            is AddTransactionEvent.OnDateClick -> {

            }

            is AddTransactionEvent.OnNoteChange -> {

            }

            is AddTransactionEvent.OnRecurringChange -> {

            }

            is AddTransactionEvent.OnTypeChange -> {
                _state.update {
                    it.copy(type = event.type)
                }
            }
        }
    }
}