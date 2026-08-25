package com.example.nexcashapplication.feature.category.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.category.domain.usecase.CategoryUseCases
import com.example.nexcashapplication.feature.category.presentation.detail.DetailCategoryEffect.NavigateToAddSubCategory
import com.example.nexcashapplication.feature.category.presentation.detail.DetailCategoryEffect.NavigateToDeleteCategory
import com.example.nexcashapplication.feature.category.presentation.detail.DetailCategoryEffect.NavigateToEditCategory
import com.example.nexcashapplication.feature.category.presentation.mapper.toUiModel
import com.example.nexcashapplication.feature.transaction.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.transaction.presentation.main.mapper.toItemUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailCategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val categoryUseCases: CategoryUseCases,
) : BaseViewModel<DetailCategoryState, DetailCategoryEvent, DetailCategoryEffect>(
    DetailCategoryState(),
) {

    private val route: Route.CategoryRoute.Detail = savedStateHandle.toRoute()
    private val categoryId = route.categoryId

    init {
        loadDetailCategory(categoryId)
        handleWeeklyTrendObserver()
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
            is DetailCategoryEvent.OnSubCategoryClick -> {
                viewModelScope.launch {
                    sendEffect(
                        if (event.subCategoryId != null){
                            DetailCategoryEffect.NavigateToSubCategoryDetail(event.subCategoryId)
                        }else{
                            DetailCategoryEffect.ShowSnackbar("Sub Category ID is null")
                        },
                    )
                }
            }

            is DetailCategoryEvent.OnTransactionClick -> {
                viewModelScope.launch {
                    sendEffect(
                        if (event.transactionId != null) {
                            DetailCategoryEffect.NavigateToTransactionDetail(event.transactionId)
                        }else{
                            DetailCategoryEffect.ShowSnackbar("Transaction ID is null")
                        }
                    )
                }
            }
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
                            updateState { it.copy(category = detailCategory?.toUiModel()) }
                        }
            } catch (e: Exception) {
                updateState { it.copy(isLoading = false, error = e.message) }
            }
        }

        viewModelScope.launch {
            try {
                categoryRepository.getSubCategories(categoryId)
                        .catch { e -> updateState { it.copy(error = e.message) } }
                        .collect { subCategory ->
                            updateState { categoryState ->
                                categoryState.copy(subCategories = subCategory.map { it.toUiModel() })
                            }
                        }
            } catch (e: Exception) {
                updateState { it.copy(isLoading = false, error = e.message) }
            }
        }

        transactionRepository.getRecentTransactions(5)
                .onEach { transactionsWithDetail ->
                    updateState {
                        it.copy(
                            recentTransactions = transactionsWithDetail.map { it.toItemUiModel() }
                        )
                    }
                }
                .launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun handleWeeklyTrendObserver() {
        viewModelScope.launch {
            state
                    .map { it.category?.typeCategory?.name?.lowercase() }
                    .distinctUntilChanged()
                    .filterNotNull() // atau handle null case sesuai kebutuhan
                    .flatMapLatest { typeTransaction ->
                        categoryRepository.getWeeklyTrend(
                            typeTransaction = typeTransaction,
                            categoryId = categoryId,
                        )
                    }
                    .catch { e -> updateState { it.copy(error = e.message) } }
                    .collect { trend ->
                        updateState {
                            it.copy(
                                weeklyData = trend.data,
                                weeklyLabels = trend.labels,
                            )
                        }
                    }
        }
    }
}
