package com.example.nexcashapplication.feature.transaction.presentation.addnew

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.aset.domain.repository.AccountRepository
import com.example.nexcashapplication.feature.aset.presentation.main.mapper.toAccountUiModel
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.transaction.presentation.addnew.mapper.toAddEditTransactionState
import com.example.nexcashapplication.feature.transaction.presentation.addnew.mapper.toUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Instant

@HiltViewModel
class AddEditTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<AddEditTransactionState, AddEditTransactionEvent, AddEditTransactionEffect>(
    initialState = AddEditTransactionState(),
) {
    private val route: Route.TransactionRoute.AddEdit = savedStateHandle.toRoute()
    private val transactionId = route.transactionId

    init {
        observeTransaction(transactionId)
    }


    override fun onEvent(event: AddEditTransactionEvent) {
        when (event) {

            is AddEditTransactionEvent.OnTimeClick -> {
                updateState {
                    it.copy(
                        showTimePicker = !it.showTimePicker,
                    )
                }
            }

            is AddEditTransactionEvent.OnSaveClick -> {
                handleSave()
            }

            is AddEditTransactionEvent.OnAccountClick -> {
                val willShow = !state.value.showAccountBottomSheet
                updateState {
                    it.copy(
                        showAccountBottomSheet = willShow,
                    )
                }

                if (willShow && state.value.availableAccounts.isEmpty()) {
                    viewModelScope.launch {
                        ensureAccountsLoaded()
                    }
                }
            }

            is AddEditTransactionEvent.OnAccountSelected -> {
                updateState {
                    it.copy(
                        selectedAccountId = event.account.id,
                        showAccountBottomSheet = false,
                    )
                }
            }

            is AddEditTransactionEvent.OnCategoryClick -> {
                val willShow = !state.value.showCategoryBottomSheet

                updateState {
                    it.copy(
                        showCategoryBottomSheet = willShow,
                    )
                }

                if (willShow) {
                    viewModelScope.launch {
                        ensureCategoriesLoaded()
                    }
                }
            }

            is AddEditTransactionEvent.OnAttachmentClick -> {
                // Implement attachment logic
            }

            is AddEditTransactionEvent.OnBackClick -> {
                viewModelScope.launch {
                    sendEffect(AddEditTransactionEffect.NavigateBack)
                }
            }

            is AddEditTransactionEvent.OnDateClick -> {
                updateState {
                    it.copy(
                        showDatePicker = !it.showDatePicker,
                    )
                }
            }

            is AddEditTransactionEvent.OnNoteChange -> {
                updateState {
                    it.copy(note = event.note)
                }
            }

            is AddEditTransactionEvent.OnRecurringChange -> {
                updateState {
                    it.copy(isRecurring = event.isRecurring)
                }
            }

            is AddEditTransactionEvent.OnTypeChange -> {
                updateState {
                    it.copy(type = event.type)
                }
            }

            is AddEditTransactionEvent.OnNameTransactionChange -> {
                updateState {
                    it.copy(
                        nameTransaction = event.name,
                    )
                }
            }

            is AddEditTransactionEvent.OnAmountChange -> {
                updateState {
                    it.copy(
                        amount = event.amount.toLongOrNull() ?: 0,
                    )
                }
            }

            is AddEditTransactionEvent.OnDateChange -> {
                updateState {
                    it.copy(
                        transactionAt = Instant.fromEpochMilliseconds(event.date),
                    )
                }
            }

            is AddEditTransactionEvent.OnTimeChange -> {
                updateState {
                    it.copy(
                        transactionAt = event.time,
                    )
                }
            }

            is AddEditTransactionEvent.OnCategorySelected -> {
                val categoryId = event.category.id
                val parentCategory =
                    state.value.availableCategories.find { it.categoriItem.id == categoryId }
                val hasSubCategories = parentCategory?.subCategoriesItem?.isNotEmpty() == true

                updateState {
                    it.copy(
                        selectedCategoryId = categoryId,
                        showCategoryBottomSheet = hasSubCategories,
                    )
                }
            }

        }
    }

    private fun handleSave() {
        if (!state.value.isValid) return

        viewModelScope.launch {
            val currentState = state.value

            val transaction = Transaction(
                id = currentState.transactionId, // sesuaikan strategi ID-mu
                title = currentState.nameTransaction,
                amount = currentState.amount,
                categoryId = currentState.selectedCategoryId,
                description = currentState.note,
                typeTransaction = currentState.type,
                accountId = currentState.selectedAccountId ?: "",
                dateTime = currentState.transactionAt,
            )

            Log.d("transaciton", "handleSave: transaciton id = ${currentState.transactionId}")
            if (currentState.transactionId != null) {
                transactionRepository.updateTransaction(transaction)
            } else {
                transactionRepository.insertTransaction(transaction)
            }

            sendEffect(AddEditTransactionEffect.NavigateBack)
        }
    }

    private fun observeTransaction(id: String?) {
        viewModelScope.launch {
            if (id != null) {
                launch {
                    ensureCategoriesLoaded()
                }
                launch {
                    ensureAccountsLoaded()
                }

                transactionRepository.getTransactionWithDetailsById(id).collect { transaction ->
                    // sementara pakai let
                    transaction?.let {
                        updateState { current ->
                            transaction.toAddEditTransactionState(
                                availableCategories = current.availableCategories,
                                availableAccounts = current.availableAccounts,
                            ).copy(
                                showDatePicker = current.showDatePicker,
                                showTimePicker = current.showTimePicker,
                                showCategoryBottomSheet = current.showCategoryBottomSheet,
                                showAccountBottomSheet = current.showAccountBottomSheet,
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun ensureCategoriesLoaded() {
        if (state.value.availableCategories.isNotEmpty()) return

        runCatching {
            categoryRepository.getCategoriesAndSubCategories()
        }.onSuccess { result ->
            updateState {
                it.copy(availableCategories = result.map { model -> model.toUIModel() })
            }
        }
    }

    private suspend fun ensureAccountsLoaded() {
        if (state.value.availableAccounts.isNotEmpty()) return

        runCatching {
            accountRepository.getAllAccounts().first()
        }.onSuccess { result ->
            updateState {
                it.copy(availableAccounts = result.map { it.toAccountUiModel() })
            }
        }
    }
}
