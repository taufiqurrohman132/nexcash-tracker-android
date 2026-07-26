package com.example.nexcashapplication.feature.category.presentation.addnew.category

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.datasource.local.datastore.PreferencesKeys
import com.example.nexcashapplication.core.domain.repository.PreferencesRepository
import com.example.nexcashapplication.core.extension.moveToFront
import com.example.nexcashapplication.core.extension.toColor
import com.example.nexcashapplication.core.extension.toHex
import com.example.nexcashapplication.feature.category.domain.repository.CategoryRepository
import com.example.nexcashapplication.feature.category.presentation.mapper.toDomain
import com.example.nexcashapplication.feature.category.presentation.mapper.toUiState
import com.example.nexcashapplication.feature.category.presentation.navigation.CategoryNavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import javax.inject.Inject

@HiltViewModel
class AddCategoryViewModel @Inject constructor(
    val repository: CategoryRepository,
    val preferencesRepository: PreferencesRepository,
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
            val currentState = _state.value

            try {
                val category = repository.getCategoryById(id)
                if (category != null) {
                    _state.update {
                        category.toUiState(
                            recentColors = getRecentColorsSnapshot().moveToFront(
                                color = currentState.selectedColor,
                            ),
                        )
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
                // simpan
                val category = currentState.toDomain(id = currentState.id)
                val colorSelected = currentState.selectedColor
                val recentColors = currentState.recentColors.map { it.toHex() }

                saveRecentColors(recentColors)
                repository.insertCategory(category)
            } catch (e: Exception) {
                Log.e(TAG, "save: $e")
            }
//            finally {
//                _state.update { it.copy(isLoading = false) }
//            }
        }
    }

    suspend fun saveRecentColors(colors: List<String>) {
        preferencesRepository.saveList(
            key = PreferencesKeys.RECENT_COLORS,
            value = colors,
            serializer = ListSerializer(String.serializer()),
        )
    }

    suspend fun getRecentColorsSnapshot(): List<Color> =
        preferencesRepository
                .getList(PreferencesKeys.RECENT_COLORS, ListSerializer(String.serializer()))
                .map { hexList -> hexList.map { it.toColor() } }
                .first()

    companion object {
        private val TAG = AddCategoryViewModel::class.java.simpleName
    }
}