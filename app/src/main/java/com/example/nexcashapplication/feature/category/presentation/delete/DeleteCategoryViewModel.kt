package com.example.nexcashapplication.feature.category.presentation.delete

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.category.domain.model.CategoryDeleteResolution
import com.example.nexcashapplication.feature.category.domain.repository.CategoryRepository
import com.example.nexcashapplication.feature.category.presentation.delete.model.DeleteOption
import com.example.nexcashapplication.feature.category.presentation.mapper.toCategoryDeleteItemList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteCategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {
    private val route: Route.CategoryRoute.Delete = savedStateHandle.toRoute()
    private val categoryId = route.categoryId
    private val typeCategory = route.typeCategory

    private val _state = MutableStateFlow(DeleteCategoryState())
    val state: StateFlow<DeleteCategoryState> = _state

    private val _effect = Channel<DeleteCategoryEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val category = categoryRepository.getCategoryById(categoryId)
//            val count = categoryRepository.countTransactionsByCategory(categoryId) masih eror

            category.collect { data ->
                val listCategories =
                    categoryRepository.getParentCategoriesNameByType(typeCategory.name.lowercase())
                            .filter { it.id != data?.id }
                            .toCategoryDeleteItemList()

                _state.value = _state.value.copy(
                    categoryId = categoryId,
                    categoryName = data?.name ?: "",
                    transactionCount = 0,
                    availableCategories = listCategories,
                )
            }
        }
    }

    fun onEvent(event: DeleteCategoryEvent) {
        when (event) {
            DeleteCategoryEvent.Cancel -> {
                viewModelScope.launch {
                    _effect.send(DeleteCategoryEffect.NavigateBack)
                }
            }

            DeleteCategoryEvent.ConfirmDelete -> {
                confirmDelete()
            }

            is DeleteCategoryEvent.SelectOption -> {
                _state.update {
                    it.copy(
                        selectedOption = event.option,
                        isDropdownExpanded = false,
                    )
                }
            }

            is DeleteCategoryEvent.SelectTargetCategory -> {
                _state.update {
                    it.copy(
                        targetCategory = event.category,
                        isDropdownExpanded = false,
                    )
                }
            }

            DeleteCategoryEvent.ToggleDropdown -> {
                _state.update {
                    it.copy(
                        isDropdownExpanded = !it.isDropdownExpanded,
                    )
                }
            }
        }
    }

    private fun confirmDelete() {
        val currentStete = _state.value
        if (!currentStete.isConfirmEnabled) return

        val resolution = when (currentStete.selectedOption) {
            DeleteOption.DELETE_ALL_TRANSACTIONS -> CategoryDeleteResolution.DeleteAllTransactions
            DeleteOption.MOVE_TO_CATEGORY -> CategoryDeleteResolution.MoveToCategory(
                requireNotNull(currentStete.targetCategory).id,
            )

            DeleteOption.UNCATEGORIZED -> CategoryDeleteResolution.Uncategorize
        }

        viewModelScope.launch {
            _state.value = currentStete.copy(isSubmitting = true)
            try {
                categoryRepository.deleteCategory(currentStete.categoryId, resolution)
                _effect.send(DeleteCategoryEffect.DeleteSuccess)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isSubmitting = false)
                _effect.send(
                    DeleteCategoryEffect.ShowError(
                        e.message ?: "Gagal menghapus kategori",
                    ),
                )
            }
        }
    }

}