package com.example.nexcashapplication.feature.category.presentation.delete

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.core.i18n.toUiText
import com.example.nexcashapplication.core.i18n.uiText
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.category.domain.model.CategoryDeleteResolution
import com.example.nexcashapplication.feature.category.domain.usecase.CategoryUseCases
import com.example.nexcashapplication.feature.category.presentation.delete.model.DeleteOption
import com.example.nexcashapplication.feature.category.presentation.mapper.toCategoryDeleteItemList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteCategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository,
    private val categoryUseCases: CategoryUseCases,
) : BaseViewModel<DeleteCategoryState, DeleteCategoryEvent, DeleteCategoryEffect>(
    DeleteCategoryState(),
) {
    private val route: Route.CategoryRoute.Delete = savedStateHandle.toRoute()
    private val categoryId = route.categoryId
    private val typeCategory = route.typeCategory

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                val transactionCount = categoryRepository.countTransactionsByCategory(categoryId)

                categoryUseCases.getCategoryById(categoryId).collect { data ->
                    val listCategories =
                        categoryRepository
                                .getParentCategoriesNameByType(typeCategory.name.lowercase())
                                .filter { it.id != data?.id }
                                .toCategoryDeleteItemList()

                    updateState {
                        it.copy(
                            categoryId = categoryId,
                            categoryName = data?.name ?: "",
                            transactionCount = transactionCount,
                            availableCategories = listCategories,
                            isLoading = false,
                        )
                    }
                }
            } catch (e: Exception) {
                updateState { it.copy(isLoading = false) }
                sendEffect(
                    DeleteCategoryEffect.ShowError(uiText(R.string.cat_load_failed)),
                )
            }
        }
    }

    override fun onEvent(event: DeleteCategoryEvent) {
        when (event) {
            DeleteCategoryEvent.Cancel -> {
                viewModelScope.launch {
                    sendEffect(DeleteCategoryEffect.NavigateBack)
                }
            }

            DeleteCategoryEvent.ConfirmDelete -> {
                confirmDelete()
            }

            is DeleteCategoryEvent.SelectOption -> {
                updateState {
                    it.copy(
                        selectedOption = event.option,
                        isDropdownExpanded = false,
                    )
                }
            }

            is DeleteCategoryEvent.SelectTargetCategory -> {
                updateState {
                    it.copy(
                        targetCategory = event.category,
                        isDropdownExpanded = false,
                    )
                }
            }

            DeleteCategoryEvent.ToggleDropdown -> {
                updateState {
                    it.copy(
                        isDropdownExpanded = !it.isDropdownExpanded,
                    )
                }
            }
        }
    }

    private fun confirmDelete() {
        val currentState = state.value
        if (!currentState.isConfirmEnabled) return

        val resolution = when (currentState.selectedOption) {
            DeleteOption.DELETE_ALL_TRANSACTIONS -> CategoryDeleteResolution.DeleteAllTransactions
            DeleteOption.MOVE_TO_CATEGORY -> CategoryDeleteResolution.MoveToCategory(
                requireNotNull(currentState.targetCategory).id,
            )

            DeleteOption.UNCATEGORIZED -> CategoryDeleteResolution.Uncategorize
        }

        viewModelScope.launch {
            updateState { it.copy(isSubmitting = true) }
            try {
                categoryUseCases.deleteCategory(currentState.categoryId, resolution)
                sendEffect(DeleteCategoryEffect.DeleteSuccess)
            } catch (e: Exception) {
                updateState { it.copy(isSubmitting = false) }
                sendEffect(
                    DeleteCategoryEffect.ShowError(e.toUiText(R.string.cat_delete_failed)),
                )
            }
        }
    }

}
