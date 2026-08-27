package com.example.nexcashapplication.feature.budgets.presentation.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddEditBudgetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,

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
            is AddEditBudgetEvent.BerulangToggled -> TODO()
            AddEditBudgetEvent.ErrorShown -> TODO()
            is AddEditBudgetEvent.PeriodeTipeChanged -> TODO()
            AddEditBudgetEvent.SimpanClicked -> TODO()
            is AddEditBudgetEvent.TanggalBerakhirChanged -> TODO()
            is AddEditBudgetEvent.TanggalMulaiChanged -> TODO()
        }
    }


}