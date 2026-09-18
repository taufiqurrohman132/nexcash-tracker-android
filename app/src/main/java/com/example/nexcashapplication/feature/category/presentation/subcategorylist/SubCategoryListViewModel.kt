package com.example.nexcashapplication.feature.category.presentation.subcategorylist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.category.presentation.mapper.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubCategoryListViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
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

    private fun observerSubCategories() {
        categoryRepository.getSubCategories(categoryId)
                .onEach { subCategories ->
                    updateState {
                        it.copy(
                            parentCategoryName = route.categoryName,
                            subCategories = subCategories.map { category -> category.toUiModel() },
                            isLoading = false,
                        )
                    }
                }
                .launchIn(viewModelScope)
    }
}