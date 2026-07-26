package com.example.nexcashapplication.feature.category.presentation.addnew.category

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.feature.category.domain.repository.CategoryRepository
import com.example.nexcashapplication.feature.category.presentation.mapper.toDomain
import com.example.nexcashapplication.feature.category.presentation.mapper.toUiState
import com.example.nexcashapplication.feature.category.presentation.navigation.CategoryNavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCategoryViewModel @Inject constructor(
    val repository: CategoryRepository,
) : ViewModel() {
    private var _state = MutableStateFlow(AddCategoryState())
    val state: StateFlow<AddCategoryState> = _state.asStateFlow()

    private var _navigationEvent = Channel<CategoryNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        val id = _state.value.id
        if (id.isNotBlank()) {
            loadCategory(id)
        }
    }

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
                save()

                viewModelScope.launch {
                    _navigationEvent.send(CategoryNavigationEvent.NavigateBack)
                }
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
                _state.update {
                    it.copy(selectedIcon = event.icon)
                }
            }

            is AddCategoryEvent.OnColorSelected -> {
                _state.update {
                    it.copy(selectedColor = event.color)
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

    fun loadCategory(id: String) {
        viewModelScope.launch {

            try {
                val category = repository.getCategoryById(id)
                if (category != null) {
                    _state.update {
                        category.toUiState()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "loadCategory: $e")
            }
        }
    }

    fun save() {
        val currentState = _state.value

        if (!currentState.isValid) {
            viewModelScope.launch {
                // effect error dll
            }
            return
        }

        viewModelScope.launch {
            try {
                val category = currentState.toDomain(id = currentState.id)
                repository.insertCategory(category)
            } catch (e: Exception) {
                Log.e(TAG, "save: $e")
            }
//            finally {
//                _state.update { it.copy(isLoading = false) }
//            }
        }
    }

    companion object {
        private val TAG = AddCategoryViewModel::class.java.simpleName
    }
}