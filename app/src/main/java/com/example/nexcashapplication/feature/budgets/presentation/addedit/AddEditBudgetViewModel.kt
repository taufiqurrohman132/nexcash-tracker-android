package com.example.nexcashapplication.feature.budgets.presentation.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.repository.BudgetRepository
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import com.example.nexcashapplication.feature.budgets.domain.usecase.GetBudgetPeriodUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddEditBudgetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val budgetRepository: BudgetRepository,
    private val getBudgetPeriodUseCase: GetBudgetPeriodUseCase,
) : BaseViewModel<AddEditBudgetState, AddEditBudgetEvent, AddEditBudgetEffect>(
    AddEditBudgetState(),
) {
    private val route: Route.BudgetRoute.AddEdit = savedStateHandle.toRoute()
    private val budgetId = route.budgetId
    private val categoryId = route.categoryId

    private val currentStete get() = state.value

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

            is AddEditBudgetEvent.BatasBaruChanged -> {
                updateState {
                    it.copy(
                        batasBaruParsed = event.rawInput.toLongOrNull() ?: 0L,
                    )
                }
            }

            is AddEditBudgetEvent.BerulangToggled -> {
                updateState {
                    it.copy(
                        isBerulang = event.enabled,
                    )
                }
            }

            AddEditBudgetEvent.ErrorShown -> TODO()
            is AddEditBudgetEvent.PeriodeTipeChanged -> updateState {
                it.copy(
                    periodeTipe = event.tipe,
                    isBerulangToggleEnabled = event.tipe == RecurrenceType.CUSTOM,
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
                        tanggalMulai = if (event.date <= it.tanggalMulai) event.date.minusDays(1) else it.tanggalMulai,
                    )
                }
            }

            is AddEditBudgetEvent.TanggalMulaiChanged -> {
                updateState {
                    it.copy(
                        tanggalMulai = event.date,
                        tanggalBerakhir = if (event.date >= it.tanggalBerakhir) event.date.plusDays(
                            1,
                        ) else it.tanggalBerakhir,
                    )
                }
            }

            is AddEditBudgetEvent.TanggalBerakhirClicked -> {
                updateState {
                    it.copy(
                        activeDateDialog = DateFieldTarget.TanggalAkhir,
                    )
                }
            }

            is AddEditBudgetEvent.TanggalMulaiClicked -> {
                updateState {
                    it.copy(
                        activeDateDialog = DateFieldTarget.TanggalMulai,
                    )
                }
            }

            AddEditBudgetEvent.TanggalDismissClicked -> {
                updateState {
                    it.copy(
                        activeDateDialog = null,
                    )
                }
            }
        }
    }

    private fun handleSave() {
        val currentState = state.value
        val (startDate, endDate) = getBudgetPeriodUseCase(
            isRecurring = currentState.isBerulang,
            type = currentState.periodeTipe,
            startDate = currentState.tanggalMulai,
            endDate = currentState.tanggalBerakhir,
        )

        viewModelScope.launch {
            if (categoryId != null) {
                val budget = Budget(
                    categoryId = categoryId,
                    startDate = startDate,
                    endDate = endDate,
                    amount = currentState.batasBaruParsed,
                    recurrenceType = currentState.periodeTipe,
                    isRecurring = currentState.isBerulang,
                )

                budgetRepository.insertBudget(budget)
            }
            sendEffect(AddEditBudgetEffect.NavigateBack)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            if (categoryId != null) {
                val today = LocalDate.now()
                budgetRepository.getActiveBudgetForCategory(categoryId, today)
                        .collect { activeBudget ->
                            if (activeBudget != null) {
                                updateState {
                                    it.copy(
                                        periodeTipe = activeBudget.recurrenceType,
                                        tanggalMulai = activeBudget.startDate,
                                        tanggalBerakhir = activeBudget.endDate
                                            ?: activeBudget.startDate, /// sementararaa
                                        batasBaruParsed = activeBudget.amount,
                                    )
                                }
                            }
                        }
            }
        }
    }


}