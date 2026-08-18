package com.example.nexcashapplication.feature.transaction.presentation.addnew

import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.repository.TransactionRepository
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class AddEditTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
) : BaseViewModel<AddEditTransactionState, AddEditTransactionEvent, AddEditTransactionEffect>(
    initialState = AddEditTransactionState(),
) {


    override fun onEvent(event: AddEditTransactionEvent) {
        when (event) {
            is AddEditTransactionEvent.OnAmountClick -> {

            }

            is AddEditTransactionEvent.OnTimeClick -> {

            }

            is AddEditTransactionEvent.OnSaveClick -> {
                handleSave()
            }

            is AddEditTransactionEvent.OnAccountClick -> {
            }

            is AddEditTransactionEvent.OnCategoryClick -> {
            }

            is AddEditTransactionEvent.OnAttachmentClick -> {

            }

            is AddEditTransactionEvent.OnBackClick -> {
                viewModelScope.launch {
                    sendEffect(AddEditTransactionEffect.NavigateBack)
                }
            }

            is AddEditTransactionEvent.OnDateClick -> {

            }

            is AddEditTransactionEvent.OnNoteChange -> {

            }

            is AddEditTransactionEvent.OnRecurringChange -> {

            }

            is AddEditTransactionEvent.OnTypeChange -> {
                updateState {
                    it.copy(type = event.type)
                }
            }

            is AddEditTransactionEvent.OnNameTransactionChange -> {

            }
        }
    }

    private fun handleSave() {
        viewModelScope.launch {
            val currentState = state.value

            val transaction = Transaction(
                title = currentState.nameTransaction,
                amount = currentState.amountDisplay,
                categoryId = currentState.category,
                description = currentState.note,
                isIncome = currentState.type == TransactionType.INCOME,
                accountId = currentState.account,
            )
            transactionRepository.insertTransaction(transaction)
        }
    }
}