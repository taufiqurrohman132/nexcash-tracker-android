package com.example.nexcashapplication.feature.category.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.feature.category.domain.repository.CategoryRepository
import com.example.nexcashapplication.feature.category.presentation.mapper.toUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = DetailCategoryViewModel.Factory::class)
class DetailCategoryViewModel @AssistedInject constructor(
    val categoryRepository: CategoryRepository,
    @Assisted private val categoryId: String,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(categoryId: String): DetailCategoryViewModel
    }

    private var _state = MutableStateFlow(DetailCategoryState())
    val state: StateFlow<DetailCategoryState> = _state.asStateFlow()

    private var _detailCategoryEffect = Channel<DetailCategoryEffect>()
    val detailCategoryEffect = _detailCategoryEffect.receiveAsFlow()

    init {
        loadDetailCategory(categoryId)
    }

    fun onEvent(event: DetailCategoryEvent) {
        when (event) {
            DetailCategoryEvent.OnAddTransactionClick -> {

            }

            DetailCategoryEvent.OnBackClick -> {
                viewModelScope.launch {
                    _detailCategoryEffect.send(DetailCategoryEffect.NavigateBack)
                }
            }

            DetailCategoryEvent.OnEditClick -> {
                viewModelScope.launch {
                    _detailCategoryEffect.send(
                        DetailCategoryEffect.NavigateToEditCategory(
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
                    _detailCategoryEffect.send(
                        DetailCategoryEffect.NavigateToAddSubCategory(
                            categoryId,
                        ),
                    )
                }

            }
        }
    }

    fun loadDetailCategory(categoryId: String) {
        viewModelScope.launch {
            try {
                categoryRepository.getCategoryById(id = categoryId)
                        .catch { e -> _state.update { it.copy(error = e.message) } }
                        .collect { detailCategory ->
                            _state.update {
                                it.copy(
                                    category = detailCategory?.toUiModel(),
                                )
                            }
                        }

                // subcategory tetap Flow karena mungkin butuh observe realtime
                // (misal saat nambah/edit/hapus subkategori)
                categoryRepository.getSubCategories(categoryId)
                        .catch { e -> _state.update { it.copy(error = e.message) } }
                        .collect { subCategory ->
                            _state.update {
                                it.copy(subCategories = subCategory.map { it.toUiModel() })
                            }
                        }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }


}