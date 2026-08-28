package com.example.nexcashapplication.feature.budgets.presentation.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.repository.BudgetRepository
import com.example.nexcashapplication.core.navigations.Route
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
    private val route: Route.BudgetRoute.AddEdit = savedStateHandle.toRoute()
    private val budgetId = route.budgetId

    init {
        loadData()
    }

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

            AddEditBudgetEvent.TanggalDismissClicked -> {
                updateState {
                    it.copy(
                        showDateDialog = false,
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
                val budget = Budget(
                    categoryId = currentState.categoryId,
                    startDate = startDate,
                    endDate = endDate,
                    amount = currentState.batasBaruParsed,
                    recurrenceType = currentState.periodeTipe,
                )

                if (budgetId != null) {
                    budgetRepository.insertBudget(
                        budget,
                    )
                } else {
                    budgetRepository.updateBudget(budget)
                }
            }
            sendEffect(AddEditBudgetEffect.NavigateBack)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            if (budgetId != null) {
                val budget = budgetRepository.getById(budgetId)

                if (budget != null) {
                    updateState {
                        it.copy(
                            categoryId = budget.categoryId,
                            periodeTipe = budget.recurrenceType,
                            tanggalMulai = budget.startDate,
                            tanggalBerakhir = budget.endDate,
                            batasBaruParsed = budget.amount,
                        )
                    }
                }
            }
        }
    }


}