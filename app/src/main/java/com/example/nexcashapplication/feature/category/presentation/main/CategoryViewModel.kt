package com.example.nexcashapplication.feature.category.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.feature.category.domain.usecase.CategoryUseCases
import com.example.nexcashapplication.feature.category.presentation.main.CategoryEffect.NavigateToDetail
import com.example.nexcashapplication.feature.category.presentation.mapper.toCategoryItemList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryUseCases: CategoryUseCases,
    private val categoryRepository: com.example.nexcashapplication.feature.category.domain.repository.CategoryRepository,
) : BaseViewModel<CategoryState, CategoryEvent, CategoryEffect>(CategoryState()) {

    init {
        observeCategories()
        observeSearch()
    }

    override fun onEvent(event: CategoryEvent) {
        when (event) {
            is CategoryEvent.OnRefresh -> {
                // TODO: Implement refresh logic
            }

            is CategoryEvent.OnKategoriClick -> {
                viewModelScope.launch {
                    sendEffect(NavigateToDetail(event.id))
                }
            }

            is CategoryEvent.OnAddKategoriClick -> {
                viewModelScope.launch {
                    sendEffect(CategoryEffect.NavigateToAddCategory(categoryId = ""))
                }
            }

            is CategoryEvent.OnSearchQueryChange -> {
                updateState {
                    it.copy(
                        searchQuery = event.query,
                    )
                }
            }

            is CategoryEvent.OnTabCategoryClick -> {
                updateState {
                    it.copy(
                        selectedTabCategory = event.type,
                    )
                }
            }

            is CategoryEvent.OnPageChanged -> {
                val type = com.example.nexcashapplication.core.domain.model.TypeCategory.entries.getOrNull(event.index) ?: com.example.nexcashapplication.core.domain.model.TypeCategory.EXPENSE
                updateState { it.copy(selectedTabCategory = type) }
            }
        }
    }

    private fun observeCategories() {
        viewModelScope.launch {
            categoryRepository.getExpenseCategories()
                .map { it.toCategoryItemList() }
                .collect { items ->
                    updateState { it.copy(expenseList = items, isLoading = false) }
                }
        }
        viewModelScope.launch {
            categoryRepository.getIncomeCategories()
                .map { it.toCategoryItemList() }
                .collect { items ->
                    updateState { it.copy(incomeList = items, isLoading = false) }
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun observeSearch() {
        viewModelScope.launch {
            state.map { it.searchQuery to it.selectedTabCategory }
                .distinctUntilChanged()
                .debounce(300.milliseconds)
                .flatMapLatest { (query, tab) ->
                    if (query.isBlank()) {
                        flowOf(emptyList())
                    } else {
                        categoryRepository.searchCategories(query, tab.name.lowercase())
                    }
                }
                .map { it.toCategoryItemList() }
                .collect { result ->
                    updateState {
                        it.copy(
                            categorySearchList = result,
                        )
                    }
                }
        }
    }
}
