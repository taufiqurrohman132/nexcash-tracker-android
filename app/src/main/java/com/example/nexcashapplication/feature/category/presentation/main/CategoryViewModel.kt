package com.example.nexcashapplication.feature.category.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.feature.category.presentation.navigation.CategoryNavigationEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CategoryViewModel: ViewModel() {
    private var _state = MutableStateFlow(
        CategoryState(

        )
    )
     val state: StateFlow<CategoryState> = _state.asStateFlow()

    private var _navigationEvent = Channel<CategoryNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun onEvent(event: CategoryEvent) {
        when(event){
            is CategoryEvent.OnRefresh -> {

            }
            is CategoryEvent.OnFilterSelect -> {

            }
            is CategoryEvent.OnKategoriClick -> {
                viewModelScope.launch {
                    _navigationEvent.send(CategoryNavigationEvent.NavigateToDetail(event.id))
                }
            }
            is CategoryEvent.OnAddKategoriClick -> {
                viewModelScope.launch {
                    _navigationEvent.send(CategoryNavigationEvent.NavigateToAddCategory)
                }
            }
            is CategoryEvent.OnSearchQueryChange -> {

            }
        }

    }
}