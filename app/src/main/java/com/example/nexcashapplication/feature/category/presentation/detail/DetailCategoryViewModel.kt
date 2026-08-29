package com.example.nexcashapplication.feature.category.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.core.ui.UiState
import com.example.nexcashapplication.core.ui.dataOrNull
import com.example.nexcashapplication.feature.category.domain.usecase.CategoryUseCases
import com.example.nexcashapplication.feature.category.presentation.detail.DetailCategoryEffect.NavigateToAddSubCategory
import com.example.nexcashapplication.feature.category.presentation.detail.DetailCategoryEffect.NavigateToDeleteCategory
import com.example.nexcashapplication.feature.category.presentation.detail.DetailCategoryEffect.NavigateToEditCategory
import com.example.nexcashapplication.feature.category.presentation.detail.DetailCategoryEffect.NavigateToSubCategoryDetail
import com.example.nexcashapplication.feature.category.presentation.detail.DetailCategoryEffect.NavigateToTransactionDetail
import com.example.nexcashapplication.feature.category.presentation.detail.DetailCategoryEffect.ShowSnackbar
import com.example.nexcashapplication.feature.category.presentation.detail.model.DetailCategoryUiModel
import com.example.nexcashapplication.feature.category.presentation.mapper.toUiModel
import com.example.nexcashapplication.feature.transaction.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.transaction.presentation.main.mapper.toItemUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
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
                        if (event.subCategoryId != null) {
                            NavigateToSubCategoryDetail(event.subCategoryId)
                        } else {
                            ShowSnackbar("Sub Category ID is null")
                        },
                    )
                }
            }

            is DetailCategoryEvent.OnTransactionClick -> {
                viewModelScope.launch {
                    sendEffect(
                        if (event.transactionId != null) {
                            NavigateToTransactionDetail(event.transactionId)
                        } else {
                            ShowSnackbar("Transaction ID is null")
                        },
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

            DetailCategoryEvent.OnDeleteClick -> {
                val category = state.value.dataState.dataOrNull?.category ?: return

                viewModelScope.launch {
                    sendEffect(
                        NavigateToDeleteCategory(
                            categoryId = categoryId,
                            typeCategory = category.typeCategory,
                        ),
                    )
                }
            }

            DetailCategoryEvent.OnAddNoteClick -> TODO()
            DetailCategoryEvent.OnEditBudgetClick -> viewModelScope.launch {
                sendEffect(
                    DetailCategoryEffect.NavigateToEditBudget(),
                )
            }

            DetailCategoryEvent.OnSetNotificationClick -> TODO()
        }
    }

    fun loadDetailCategory(categoryId: String) {
        viewModelScope.launch {
            combine(
                categoryUseCases.getCategoryById(id = categoryId),
                categoryRepository.getSubCategories(categoryId),
            ) { category, subCategories ->
                category to subCategories
            }
                    .catch { e -> updateState { it.copy(dataState = UiState.Error(e.message.toString())) } }
                    .collect { (category, subCategories) ->
                        updateState { current ->
                            val existingData = current.dataState.dataOrNull
                            current.copy(
                                dataState = UiState.Success(
                                    DetailCategoryUiModel(
                                        category = category?.toUiModel(),
                                        subCategories = subCategories.map { it.toUiModel() },
                                        recentTransactions = existingData?.recentTransactions
                                            ?: emptyList(),
                                    ),
                                ),
                            )
                        }
                    }
        }

        transactionRepository.getRecentTransactions(5)
                .onEach { transactions ->
                    updateState { current ->
                        val existingData =
                            current.dataState.dataOrNull ?: return@updateState current
                        current.copy(
                            dataState = UiState.Success(
                                existingData.copy(recentTransactions = transactions.map { it.toItemUiModel() }),
                            ),
                        )
                    }
                }
                .launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun handleWeeklyTrendObserver() {
        viewModelScope.launch {
            state
                    .mapNotNull { it.dataState.dataOrNull?.category?.typeCategory }
                    .distinctUntilChanged()
                    .flatMapLatest { typeCategory ->
                        categoryRepository.getWeeklyTrend(
                            typeTransaction = typeCategory.name.lowercase(),
                            categoryId = categoryId,
                        )
                    }
                    .catch { e ->
                        updateState { it.copy(dataState = UiState.Error(e.message.toString())) }
                    }
                    .collect { trend ->
                        updateState { current ->
                            val existingData = current.dataState.dataOrNull ?: return@updateState current
                            current.copy(
                                dataState = UiState.Success(
                                    existingData.copy(
                                        weeklyData = trend.data,
                                        weeklyLabels = trend.labels,
                                    )
                                )
                            )
                        }
                    }
        }
    }
}
