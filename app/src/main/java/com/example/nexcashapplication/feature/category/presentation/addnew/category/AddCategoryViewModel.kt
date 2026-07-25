package com.example.nexcashapplication.feature.category.presentation.addnew.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.feature.category.presentation.navigation.CategoryNavigationEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddCategoryViewModel : ViewModel() {
    private var _state = MutableStateFlow(AddCategoryState())
    val state: StateFlow<AddCategoryState> = _state.asStateFlow()

    private var _navigationEvent = Channel<CategoryNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun onEvent(event: AddCategoryEvent) {
        when (event) {
            is AddCategoryEvent.OnParentCategorySelected -> {

            }

            is AddCategoryEvent.OnBackClicked -> {
                viewModelScope.launch {
                    _navigationEvent.send(CategoryNavigationEvent.NavigateBack)
                }
            }

            is AddCategoryEvent.OnSaveClicked -> {
//                viewModelScope.launch {
//                    _navigationEvent.send(CategoryNavigationEvent.NavigateToDetail(event.id))
//                }
            }

            is AddCategoryEvent.OnCancelClicked -> {

            }

            is AddCategoryEvent.OnNameChanged -> {
                _state.update {
                    it.copy(name = event.name)
                }
            }

            is AddCategoryEvent.OnAccountSelected -> {
                _state.update {
                    it.copy(defaultAccount = event.accountId)
                }
            }

            is AddCategoryEvent.OnBudgetChanged -> {
                _state.update {
                    it.copy(budgetLimit = event.amount)
                }
            }

            is AddCategoryEvent.OnChangeIcon -> {
//                _state.update {
//                    it.copy(name = event.name)
//                }
            }

            is AddCategoryEvent.OnColorSelected -> {
                _state.update {
                    it.copy(selectedColorIndex = event.colorIndex)
                }
            }

            is AddCategoryEvent.OnNotesChanged -> {
                _state.update {
                    it.copy(note = event.notes)
                }
            }

            is AddCategoryEvent.OnPeriodSelected -> {
                _state.update {
                    it.copy(selectedPeriod = event.period)
                }
            }

            is AddCategoryEvent.OnTypeSelected -> {
                _state.update {
                    it.copy(typeCategory = event.type)
                }
            }
        }

    }
}