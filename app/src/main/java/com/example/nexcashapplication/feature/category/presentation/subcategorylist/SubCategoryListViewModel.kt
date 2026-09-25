package com.example.nexcashapplication.feature.category.presentation.subcategorylist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.category.domain.usecase.GetSubCategoriesWithBudgetUseCase
import com.example.nexcashapplication.feature.category.presentation.mapper.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubCategoryListViewModel @Inject constructor(
    private val getSubCategoriesWithBudgetUseCase: GetSubCategoriesWithBudgetUseCase,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<SubCategoryListState, SubCategoryListEvent, SubCategoryListEffect>(initialState = SubCategoryListState()) {

    private val route: Route.CategoryRoute.SubCategoryList = savedStateHandle.toRoute()
    private val categoryId = route.categoryId

    init {
        observerSubCategories()
    }

    override fun onEvent(event: SubCategoryListEvent) {
        when (event) {
            is SubCategoryListEvent.OnBackClick -> viewModelScope.launch {
                sendEffect(SubCategoryListEffect.NavigateBack)
            }

            is SubCategoryListEvent.OnSubCategoryClick -> viewModelScope.launch {
                sendEffect(
                    SubCategoryListEffect.NavigateToSubCategoryDetail(event.subCategoryId),
                )
            }

            is SubCategoryListEvent.OnRetryClick -> {

            }
        }
    }

    /**
     * Sub + budget lew [GetSubCategoriesWithBudgetUseCase] — sama dengan layar detail,
     * supaya kartu sub tidak lagi menampilkan `0% / Rp 0`.
     * Budget induk ikut dibawa untuk kontribusi sub yang belum punya budget sendiri.
     */
    private fun observerSubCategories() {
        getSubCategoriesWithBudgetUseCase(categoryId)
                .onEach { result ->
                    updateState {
                        it.copy(
                            parentCategoryName = route.categoryName,
                            parentTotalBudget = result.parentBudgetAmount,
                            subCategories = result.subCategories.map { detail ->
                                detail.category.toUiModel(
                                    totalBudget = detail.totalBudget,
                                    usedBudget = detail.usedBudget,
                                )
                            },
                            isLoading = false,
                        )
                    }
                }
                .launchIn(viewModelScope)
    }
}
