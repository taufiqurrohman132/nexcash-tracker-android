package com.example.nexcashapplication.feature.category.presentation.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.common.toEpochMillis
import com.example.nexcashapplication.core.domain.repository.BudgetRepository
import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.budgets.domain.usecase.GetBudgetPeriodUseCase
import com.example.nexcashapplication.feature.category.domain.usecase.CategoryUseCases
import com.example.nexcashapplication.feature.category.presentation.main.CategoryEffect.NavigateToDetail
import com.example.nexcashapplication.feature.category.presentation.mapper.toCategoryItem
import com.example.nexcashapplication.feature.category.presentation.mapper.toCategoryItemList
import com.example.nexcashapplication.feature.common.utils.toEndOfDayMillis
import com.example.nexcashapplication.feature.common.utils.toStartOfDayMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryUseCases: CategoryUseCases,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
    private val getBudgetPeriodUseCase: GetBudgetPeriodUseCase,

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
                    sendEffect(CategoryEffect.NavigateToAddCategory())
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
                val type =
                    com.example.nexcashapplication.core.domain.model.TypeCategory.entries.getOrNull(
                        event.index,
                    ) ?: com.example.nexcashapplication.core.domain.model.TypeCategory.EXPENSE
                updateState { it.copy(selectedTabCategory = type) }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeCategories() {
        categoryRepository.getExpenseCategories() // Flow<List<CategoryEntity>>
                .flatMapLatest { categories ->
                    val categoryIds = categories.map { it.id } // sekarang baru ada

                    combine(
                        budgetRepository.getActiveBudgetsForCategories(
                            categoryIds,
                            LocalDate.now().toEpochMillis(),
                        ),
                        transactionRepository.getTransactionsForCategories(categoryIds),
                    ) { budgets, transactions ->
                        val budgetsByCategory = budgets.associateBy { it.categoryId }

                        categories.map { category ->
                            val budget = budgetsByCategory[category.id]
                            val usedAmount = if (budget != null) {
                                val (start, end) = getBudgetPeriodUseCase(
                                    isRecurring = budget.isRecurring,
                                    startDate = budget.startDate,
                                    endDate = budget.endDate,
                                    type = budget.recurrenceType,
                                )

                                val periodStartMillis = start.toStartOfDayMillis()
                                val periodEndMillis =
                                    end.toEndOfDayMillis() // end nggak boleh nullable di titik ini — lihat poin 2

                                transactions.filter {
                                    it.categoryId == category.id && it.dateTime.toEpochMilliseconds() in periodStartMillis..periodEndMillis
                                }.sumOf { it.amount }

                            } else 0L

                            Log.d(
                                "${CategoryViewModel::class.java.simpleName}",
                                "observeCategories: used = $usedAmount, budget = ${budget?.amount ?: 0L}",
                            )
                            category.toCategoryItem(
                                usedBudget = usedAmount,
                                budget = budget?.amount ?: 0L,
                            )
                        }
                    }

                }
                .onEach { items ->
                    updateState {
                        it.copy(
                            expenseList = items,
                        )
                    }
                }.launchIn(viewModelScope)

        categoryRepository.getIncomeCategories() // Flow<List<CategoryEntity>>
                .flatMapLatest { categories ->
                    val categoryIds = categories.map { it.id } // sekarang baru ada

                    combine(
                        budgetRepository.getActiveBudgetsForCategories(
                            categoryIds,
                            LocalDate.now().toEpochMillis(),
                        ),
                        transactionRepository.getTransactionsForCategories(categoryIds),
                    ) { budgets, transactions ->
                        val budgetsByCategory = budgets.associateBy { it.categoryId }

                        categories.map { category ->
                            val budget = budgetsByCategory[category.id]
                            val usedAmount = if (budget != null) {
                                val (start, end) = getBudgetPeriodUseCase(
                                    isRecurring = budget.isRecurring,
                                    startDate = budget.startDate,
                                    endDate = budget.endDate,
                                    type = budget.recurrenceType,
                                )

                                val periodStartMillis = start.toStartOfDayMillis()
                                val periodEndMillis =
                                    end.toEndOfDayMillis() // end nggak boleh nullable di titik ini — lihat poin 2

                                transactions.filter {
                                    it.categoryId == category.id && it.dateTime.toEpochMilliseconds() in periodStartMillis..periodEndMillis
                                }.sumOf { it.amount }

                            } else 0L

                            category.toCategoryItem(
                                usedBudget = usedAmount,
                                budget = budget?.amount ?: 0L,
                            )
                        }
                    }

                }
                .onEach { items ->
                    updateState {
                        it.copy(
                            incomeList = items,
                        )
                    }
                }.launchIn(viewModelScope)
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
