package com.example.nexcashapplication.feature.category.presentation.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.common.toEpochMillis
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.repository.BudgetRepository
import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.core.i18n.toUiText
import com.example.nexcashapplication.feature.budgets.domain.usecase.GetBudgetPeriodUseCase
import com.example.nexcashapplication.feature.category.domain.usecase.CategoryUseCases
import com.example.nexcashapplication.feature.category.presentation.main.CategoryEffect.NavigateToDetail
import com.example.nexcashapplication.feature.category.presentation.main.model.CategoryItem
import com.example.nexcashapplication.feature.category.presentation.mapper.toCategoryItem
import com.example.nexcashapplication.feature.category.presentation.mapper.toCategoryItemList
import com.example.nexcashapplication.feature.common.utils.toEndOfDayMillis
import com.example.nexcashapplication.feature.common.utils.toStartOfDayMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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
        observeCategoriesByType(categoryRepository.getExpenseCategories()) { items ->
            updateState { it.copy(expenseList = items, isLoading = false) }
        }
        observeCategoriesByType(categoryRepository.getIncomeCategories()) { items ->
            updateState { it.copy(incomeList = items, isLoading = false) }
        }
    }

    /**
     * Amati satu tipe kategori (induk) lengkap dengan budget + jumlah terpakai.
     *
     * Terpakai bersifat **rollup**: transaksi anak (sub kategori) ikut dihitung ke
     * induknya supaya angka di daftar kategori selaras dengan layar detail — sebelumnya
     * transaksi yang dicatat di sub tidak pernah naik ke progress induknya.
     * Periode & budget tetap per induk (sub punya budget sendiri = limit terpisah,
     * bukan pengurang budget induk, jadi tidak ada double-count).
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeCategoriesByType(
        categoriesFlow: Flow<List<Category>>,
        onUpdate: (List<CategoryItem>) -> Unit,
    ) {
        categoriesFlow
                .flatMapLatest { categories ->
                    val parentIds = categories.map { it.id }

                    // Semua kategori (induk + sub) → petakan id anak tiap induk untuk rollup.
                    categoryRepository.getAllCategory().flatMapLatest { allCategories ->
                        val childIdsByParent = allCategories
                                .filterNotNull()
                                .mapNotNull { child ->
                                    child.parentId?.let { parentId -> parentId to child.id }
                                }
                                .groupBy({ it.first }, { it.second })
                        val familyIds = (parentIds + parentIds.flatMap { childIdsByParent[it].orEmpty() })
                                .distinct()

                        combine(
                            budgetRepository.getActiveBudgetsForCategories(
                                parentIds,
                                LocalDate.now().toEpochMillis(),
                            ),
                            transactionRepository.getTransactionsForCategories(familyIds),
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

                                    val childIds = childIdsByParent[category.id].orEmpty().toSet()
                                    transactions.filter { transaction ->
                                        val txCategoryId = transaction.categoryId
                                        val belongsToFamily = txCategoryId != null &&
                                                (txCategoryId == category.id || txCategoryId in childIds)
                                        belongsToFamily &&
                                                transaction.dateTime.toEpochMilliseconds() in periodStartMillis..periodEndMillis
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
                }
                .onEach { items -> onUpdate(items) }
                // Wajib: tanpa catch kegagalan Room bocor ke viewModelScope = crash.
                // Gagal baca mengisi `error` dan menghentikan indikator; daftar lama
                // (kalau ada) tidak dibuang supaya layar tetap menampilkan data tervalid.
                .catch { e ->
                    Log.e(
                        "${CategoryViewModel::class.java.simpleName}",
                        "gagal memuat daftar kategori",
                        e,
                    )
                    updateState {
                        it.copy(isLoading = false, error = e.toUiText(R.string.cat_load_failed))
                    }
                }
                .launchIn(viewModelScope)
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
                    .catch { e ->
                        // Gagal cari bukan fatal (daftar utama tetap tampil) — reset
                        // pencarian ke kosong, jangan crash dan jangan blokir layar.
                        Log.e(
                            "${CategoryViewModel::class.java.simpleName}",
                            "gagal mencari kategori",
                            e,
                        )
                        updateState { it.copy(categorySearchList = emptyList()) }
                    }
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
