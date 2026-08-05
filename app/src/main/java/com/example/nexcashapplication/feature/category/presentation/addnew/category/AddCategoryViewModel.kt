package com.example.nexcashapplication.feature.category.presentation.addnew.category

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.core.datasource.local.datastore.PreferencesKeys
import com.example.nexcashapplication.core.domain.repository.PreferencesRepository
import com.example.nexcashapplication.core.extension.moveToFront
import com.example.nexcashapplication.core.extension.toColor
import com.example.nexcashapplication.core.extension.toHex
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.category.domain.repository.CategoryRepository
import com.example.nexcashapplication.feature.category.presentation.addnew.category.constant.CategoryIcons
import com.example.nexcashapplication.feature.category.presentation.main.CategoryEffect
import com.example.nexcashapplication.feature.category.presentation.mapper.toDomain
import com.example.nexcashapplication.feature.category.presentation.mapper.toUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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

import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.feature.category.domain.usecase.CategoryUseCases

@HiltViewModel
class AddCategoryViewModel @Inject constructor(
    private val repository: CategoryRepository,
    private val preferencesRepository: PreferencesRepository,
    private val categoryUseCases: CategoryUseCases,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<AddCategoryState, AddCategoryEvent, CategoryEffect>(AddCategoryState()) {

    private val route: Route.CategoryRoute.AddNew = savedStateHandle.toRoute()
    private val categoryId = route.categoryId
    private val parentId = route.parentId

    init {
        parentId?.let { pid ->
            viewModelScope.launch {
                categoryUseCases.getCategoryById(id = pid)
                        .collect { detailCategory ->
                            detailCategory?.let {
                                updateState {
                                    it.copy(
                                        parentCategory = ParentCategoryUi(
                                            id = detailCategory.id,
                                            name = detailCategory.name,
                                            icon = CategoryIcons.fromId(detailCategory.icon),
                                            color = detailCategory.color.toColor(),
                                        ),
                                    )
                                }
                            }
                        }

            }
        }
        if (categoryId.isNotBlank()) {
            loadCategory(categoryId)
        }
    }

    override fun onEvent(event: AddCategoryEvent) {
        when (event) {
            is AddCategoryEvent.OnParentCategorySelected -> {

            }

            is AddCategoryEvent.OnBackClicked -> {
                viewModelScope.launch {
                    sendEffect(CategoryEffect.NavigateBack)
                }
            }

            is AddCategoryEvent.OnSaveClicked -> {
                saveOrUpdate()

                viewModelScope.launch {
                    sendEffect(CategoryEffect.NavigateBack)
                }
            }

            is AddCategoryEvent.OnCancelClicked -> {

            }

            is AddCategoryEvent.OnNameChanged -> {
                updateState {
                    it.copy(name = event.name)
                }
            }

            is AddCategoryEvent.OnAccountSelected -> {
                updateState {
                    it.copy(defaultAccount = event.accountId)
                }
            }

            is AddCategoryEvent.OnBudgetChanged -> {
                updateState {
                    it.copy(budgetLimit = event.amount)
                }
            }

            is AddCategoryEvent.OnChangeIcon -> {
                updateState {
                    it.copy(selectedIcon = event.icon)
                }
            }

            is AddCategoryEvent.OnColorSelected -> {
                updateState {
                    it.copy(selectedColor = event.color)
                }
            }

            is AddCategoryEvent.OnNotesChanged -> {
                updateState {
                    it.copy(note = event.notes)
                }
            }

            is AddCategoryEvent.OnPeriodSelected -> {
                updateState {
                    it.copy(selectedPeriod = event.period)
                }
            }

            is AddCategoryEvent.OnTypeSelected -> {
                updateState {
                    it.copy(typeCategory = event.type)
                }
            }
        }

    }

    fun loadCategory(id: String) {
        viewModelScope.launch {
            try {
                categoryUseCases.getCategoryById(id = categoryId)
                        .collect { detailCategory ->
                            if (detailCategory != null) {
                                val recentColorsSnapshot = getRecentColorsSnapshot()
                                updateState {
                                    detailCategory.toUiState(
                                        recentColors = recentColorsSnapshot.moveToFront(
                                            color = it.selectedColor,
                                        ),
                                    )
                                }
                            }
                        }
            } catch (e: Exception) {
                Log.e(TAG, "loadCategory: $e")
            }
        }
    }

    fun saveOrUpdate() {
        val currentState = state.value

        if (!currentState.isValid) {
            viewModelScope.launch {
                // effect error dll
            }
            return
        }

        viewModelScope.launch {
            try {
                // simpan
                val category = currentState.toDomain(id = currentState.id, parentId = parentId)
                val recentColors = currentState.recentColors.map { it.toHex() }

                saveRecentColors(recentColors)
                Log.d(TAG, "saveOrUpdate: state id : ${currentState.id}")
                if (currentState.id.isBlank()) {
                    repository.insertCategory(category)
                } else {
                    Log.d(TAG, "saveOrUpdate: update")
                    repository.updateCategory(category)
                }
            } catch (e: Exception) {
                Log.e(TAG, "save: $e")
            }
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