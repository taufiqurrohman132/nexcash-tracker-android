package com.example.nexcashapplication.feature.category.presentation.addnew.category

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddCategoryViewModel: ViewModel() {
    private var _state = MutableStateFlow(AddCategoryState())
    val state: StateFlow<AddCategoryState> = _state.asStateFlow()

    fun onEvent(event: AddCategoryEvent){

    }
}