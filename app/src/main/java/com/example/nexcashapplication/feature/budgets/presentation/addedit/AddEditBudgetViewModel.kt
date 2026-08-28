package com.example.nexcashapplication.feature.budgets.presentation.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.repository.BudgetRepository
import com.example.nexcashapplication.feature.budgets.domain.usecase.GetBudgetPeriodUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddEditBudgetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val budgetRepository: BudgetRepository,
    private val getBudgetPeriodUseCase: GetBudgetPeriodUseCase,
) : BaseViewModel<AddEditBudgetState, AddEditBudgetEvent, AddEditBudgetEffect>(
    AddEditBudgetState(),
) {
    override fun onEvent(event: AddEditBudgetEvent) {
        when (event) {
            AddEditBudgetEvent.BatalClicked -> {
                viewModelScope.launch {
                    sendEffect(
                        AddEditBudgetEffect.NavigateBack,
                    )
                }
            }

            is AddEditBudgetEvent.BatasBaruChanged -> TODO()
            is AddEditBudgetEvent.BerulangToggled -> {
                updateState {
                    it.copy(
                        isBerulang = !event.enabled,
                        isBerulangToggleEnabled = !event.enabled,
                    )
                }
            }

            AddEditBudgetEvent.ErrorShown -> TODO()
            is AddEditBudgetEvent.PeriodeTipeChanged -> updateState {
                it.copy(
                    periodeTipe = event.tipe,
                )
            }

            AddEditBudgetEvent.SimpanClicked -> {
                viewModelScope.launch {
                    handleSave()
                }
            }

            is AddEditBudgetEvent.TanggalBerakhirChanged -> {
                updateState {
                    it.copy(
                        tanggalBerakhir = event.date,
                    )
                }
            }

            is AddEditBudgetEvent.TanggalMulaiChanged -> {
                updateState {
                    it.copy(
                        tanggalMulai = event.date,
                    )
                }
            }

            is AddEditBudgetEvent.TanggalBerakhirClicked -> {
                updateState {
                    it.copy(
                        showDateDialog = true,
                    )
                }
            }

            is AddEditBudgetEvent.TanggalMulaiClicked -> {
                updateState {
                    it.copy(
                        showDateDialog = true,
                    )
                }
            }
        }
    }

    private fun handleSave() {
        val currentState = state.value
        val (startDate, endDate) = getBudgetPeriodUseCase(
            currentState.periodeTipe,
            currentState.tanggalMulai,
            currentState.tanggalBerakhir,
        )

        viewModelScope.launch {
            if (currentState.categoryId != null) {
                budgetRepository.insertBudget(
                    Budget(
                        categoryId = currentState.categoryId,
                        startDate = startDate,
                        endDate = endDate,
                        amount = currentState.batasBaruParsed,
                        recurrenceType = currentState.periodeTipe,
                    ),
                )
            }
            sendEffect(AddEditBudgetEffect.NavigateBack)
        }
    }

    private fun loadData() {

    }


}