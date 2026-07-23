package com.example.nexcashapplication.feature.category.presentation.addnew.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.feature.category.presentation.main.CategoryEvent
import com.example.nexcashapplication.feature.category.presentation.navigation.CategoryNavigationEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddCategoryViewModel: ViewModel() {
    private var _state = MutableStateFlow(AddCategoryState())
    val state: StateFlow<AddCategoryState> = _state.asStateFlow()

    private var _navigationEvent = Channel<CategoryNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun onEvent(event: AddCategoryEvent) {
        when(event){
            is AddCategoryEvent.OnParentCategorySelected -> {

            }
            is AddCategoryEvent.OnBackClicked -> {

            }
            is AddCategoryEvent.OnSaveClicked -> {
//                viewModelScope.launch {
//                    _navigationEvent.send(CategoryNavigationEvent.NavigateToDetail(event.id))
//                }
            }
            is AddCategoryEvent.OnCancelClicked -> {

            }
            is AddCategoryEvent.OnNameChanged -> {

            }

            is AddCategoryEvent.OnAccountSelected -> {

            }
            is AddCategoryEvent.OnBudgetChanged -> {

            }
            is AddCategoryEvent.OnChangeIconColorClicked -> {

            }
            is AddCategoryEvent.OnColorSelected -> {

            }
            is AddCategoryEvent.OnNotesChanged ->{

            }
            is AddCategoryEvent.OnPeriodSelected -> {


            }
            is AddCategoryEvent.OnTypeSelected -> {

            }
        }

    }
}