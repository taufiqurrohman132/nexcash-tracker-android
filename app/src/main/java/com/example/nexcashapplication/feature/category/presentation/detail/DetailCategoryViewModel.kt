package com.example.nexcashapplication.feature.category.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.category.domain.repository.CategoryRepository
import com.example.nexcashapplication.feature.category.presentation.detail.DetailCategoryEffect.NavigateToAddSubCategory
import com.example.nexcashapplication.feature.category.presentation.detail.DetailCategoryEffect.NavigateToDeleteCategory
import com.example.nexcashapplication.feature.category.presentation.detail.DetailCategoryEffect.NavigateToEditCategory
import com.example.nexcashapplication.feature.category.presentation.mapper.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.feature.category.domain.usecase.CategoryUseCases

@HiltViewModel
class DetailCategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository,
    private val categoryUseCases: CategoryUseCases,
) : BaseViewModel<DetailCategoryState, DetailCategoryEvent, DetailCategoryEffect>(DetailCategoryState()) {

    private val route: Route.CategoryRoute.Detail = savedStateHandle.toRoute()
    private val categoryId = route.categoryId

    init {
        loadDetailCategory(categoryId)
    }

    override fun onEvent(event: DetailCategoryEvent) {
        when (event) {
            DetailCategoryEvent.OnAddTransactionClick -> {

            }

            DetailCategoryEvent.OnBackClick -> {
                viewModelScope.launch {
                    sendEffect(DetailCategoryEffect.NavigateBack)
                }
            }

            DetailCategoryEvent.OnEditClick -> {
                viewModelScope.launch {
                    sendEffect(
                        NavigateToEditCategory(
                            categoryId,
                        ),
                    )
                }
            }

            DetailCategoryEvent.OnFabAddClick -> TODO()
            DetailCategoryEvent.OnLoadMoreTransactionsClick -> TODO()
            DetailCategoryEvent.OnManageSubCategoryClick -> TODO()
            DetailCategoryEvent.OnMenuClick -> TODO()
            DetailCategoryEvent.OnReminderClick -> TODO()
            DetailCategoryEvent.OnRetryClick -> TODO()
            DetailCategoryEvent.OnSeeAllSubCategoryClick -> TODO()
            is DetailCategoryEvent.OnSubCategoryClick -> TODO()
            is DetailCategoryEvent.OnTransactionClick -> TODO()
            DetailCategoryEvent.OnAddSubCategoryClick -> {
                viewModelScope.launch {
                    sendEffect(
                        NavigateToAddSubCategory(
                            parentId = categoryId,
                        ),
                    )
                }

            }

            DetailCategoryEvent.OnDeleteClick -> viewModelScope.launch {
                sendEffect(
                    NavigateToDeleteCategory(
                        categoryId = categoryId,
                        typeCategory = state.value.category?.typeCategory ?: TypeCategory.EXPENSE,
                    ),
                )
            }
        }
    }

    fun loadDetailCategory(categoryId: String) {
        viewModelScope.launch {
            try {
                categoryUseCases.getCategoryById(id = categoryId)
                        .catch { e -> updateState { it.copy(error = e.message) } }
                        .collect { detailCategory ->
                            updateState {
                                it.copy(
                                    category = detailCategory?.toUiModel(),
                                )
                            }
                        }

                // subcategory tetap Flow karena mungkin butuh observe realtime
                // (misal saat nambah/edit/hapus subkategori)
                categoryRepository.getSubCategories(categoryId)
                        .catch { e -> updateState { it.copy(error = e.message) } }
                        .collect { subCategory ->
                            updateState {
                                it.copy(subCategories = subCategory.map { it.toUiModel() })
                            }
                        }
            } catch (e: Exception) {
                updateState { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
