package com.example.nexcashapplication.feature.category.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.category.domain.repository.CategoryRepository
import com.example.nexcashapplication.feature.category.presentation.main.CategoryEffect.NavigateToDetail
import com.example.nexcashapplication.feature.category.presentation.mapper.toCategoryItemList
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class CategoryViewModel @Inject constructor(
    val categoryRepository: CategoryRepository,
) : ViewModel() {
    private var _state = MutableStateFlow(
        CategoryState(),
    )
    val state: StateFlow<CategoryState> = _state.asStateFlow()

    private var _navigationEvent = Channel<CategoryEffect>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        observeCategories()
        observeSearch()
    }

    fun onEvent(event: CategoryEvent) {
        when (event) {
            is CategoryEvent.OnRefresh -> {

            }

            is CategoryEvent.OnKategoriClick -> {
                viewModelScope.launch {
                    _navigationEvent.send(NavigateToDetail(event.id))
                }
            }

            is CategoryEvent.OnAddKategoriClick -> {
                viewModelScope.launch {
                    _navigationEvent.send(CategoryEffect.NavigateToAddCategory(categoryId = ""))
                }
            }

            is CategoryEvent.OnSearchQueryChange -> {
                _state.update {
                    it.copy(
                        searchQuery = event.query,
                    )
                }
            }

            is CategoryEvent.OnTabCategoryClick -> {
                _state.update {
                    it.copy(
                        selectedTabCategory = event.type,
                    )
                }
            }

            is CategoryEvent.OnPageChanged -> {
                val type = TypeCategory.entries.getOrNull(event.index) ?: TypeCategory.EXPENSE
                _state.update { it.copy(selectedTabCategory = type) }
            }
        }
    }

    private fun observeCategories() {

        viewModelScope.launch {
            categoryRepository.getExpenseCategories()
                    .map { it.toCategoryItemList() }
                    .collect { items ->
                        _state.update { it.copy(expenseList = items, isLoading = false) }
                    }
        }
        viewModelScope.launch {
            categoryRepository.getIncomeCategories()
                    .map { it.toCategoryItemList() }
                    .collect { items ->
                        _state.update { it.copy(incomeList = items, isLoading = false) }
                    }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    fun observeSearch() {
        viewModelScope.launch {
            _state.map { it.searchQuery to it.selectedTabCategory }
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
                        _state.update {
                            it.copy(
                                categorySearchList = result,
                            )
                        }
                    }
        }
    }


}
