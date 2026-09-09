package com.example.nexcashapplication.feature.budgets.presentation.editmultiple

import com.example.nexcashapplication.core.common.BaseViewModel
import javax.inject.Inject

class EditPresentageBudgetViewModel @Inject constructor(

) : BaseViewModel<EditPresentageBudgetState, EditPresentageBudgetEvent, EditPresentageBudgetEffect>(
    EditPresentageBudgetState(),
) {
    override fun onEvent(event: EditPresentageBudgetEvent) {
        when (event) {
            EditPresentageBudgetEvent.OnAddCategoryClick -> TODO()
            is EditPresentageBudgetEvent.OnAmountInputChange -> TODO()
            EditPresentageBudgetEvent.OnBackClick -> TODO()
            is EditPresentageBudgetEvent.OnPeriodClick -> TODO()
            is EditPresentageBudgetEvent.OnPeriodSelected -> TODO()
            EditPresentageBudgetEvent.OnSaveClick -> TODO()
            is EditPresentageBudgetEvent.OnSliderChange -> TODO()
        }
    }

    private fun observerData(){

    }
}