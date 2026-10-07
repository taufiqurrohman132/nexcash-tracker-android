package com.example.nexcashapplication.feature.category.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.core.i18n.uiText
import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.core.ui.UiState
import com.example.nexcashapplication.core.ui.dataOrNull
import com.example.nexcashapplication.feature.category.domain.usecase.CategoryUseCases
import com.example.nexcashapplication.feature.category.domain.usecase.GetSubCategoriesWithBudgetUseCase
import com.example.nexcashapplication.feature.category.presentation.detail.DetailCategoryEffect.NavigateToAddSubCategory
import com.example.nexcashapplication.feature.category.presentation.detail.DetailCategoryEffect.NavigateToDeleteCategory
import com.example.nexcashapplication.feature.category.presentation.detail.DetailCategoryEffect.NavigateToEditCategory
import com.example.nexcashapplication.feature.category.presentation.detail.DetailCategoryEffect.NavigateToSubCategoryDetail
import com.example.nexcashapplication.feature.category.presentation.detail.DetailCategoryEffect.NavigateToTransactionDetail
import com.example.nexcashapplication.feature.category.presentation.detail.DetailCategoryEffect.ShowSnackbar
import com.example.nexcashapplication.feature.category.presentation.detail.model.DetailCategoryUiModel
import com.example.nexcashapplication.feature.category.presentation.detail.model.ParentBudgetUiModel
import com.example.nexcashapplication.feature.category.presentation.mapper.toUiModel
import com.example.nexcashapplication.feature.transaction.presentation.main.mapper.toItemUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailCategoryViewModel internal constructor(
    private val route: Route.CategoryRoute.Detail,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val categoryUseCases: CategoryUseCases,
    private val getSubCategoriesWithBudgetUseCase: GetSubCategoriesWithBudgetUseCase,
) : BaseViewModel<DetailCategoryState, DetailCategoryEvent, DetailCategoryEffect>(
    DetailCategoryState(),
) {

    /**
     * Konstruktor produksi (dipakai Hilt + Navigation): membaca typed route dari
     * [SavedStateHandle] lewat `toRoute()`.
     *
     * Catatan pengujian: `toRoute()` membaca nilai argumen lewat Bundle Android;
     * di JVM unit test Bundle di-mock sehingga `getString()` selalu null (diam-diam).
     * Karena itu unit test memakai konstruktor `internal` dengan [Route.CategoryRoute.Detail]
     * langsung — jalur di device tetap identik lewat konstruktor ini.
     */
    @Inject constructor(
        savedStateHandle: SavedStateHandle,
        transactionRepository: TransactionRepository,
        categoryRepository: CategoryRepository,
        categoryUseCases: CategoryUseCases,
        getSubCategoriesWithBudgetUseCase: GetSubCategoriesWithBudgetUseCase,
    ) : this(
        route = savedStateHandle.toRoute<Route.CategoryRoute.Detail>(),
        transactionRepository = transactionRepository,
        categoryRepository = categoryRepository,
        categoryUseCases = categoryUseCases,
        getSubCategoriesWithBudgetUseCase = getSubCategoriesWithBudgetUseCase,
    )

    private val categoryId = route.categoryId

    init {
        loadDetailCategory(categoryId)
        handleWeeklyTrendObserver()
        handleParentBudgetObserver()
    }

    override fun onEvent(event: DetailCategoryEvent) {
        when (event) {
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

            DetailCategoryEvent.OnLoadMoreTransactionsClick -> viewModelScope.launch {
                sendEffect(DetailCategoryEffect.NavigateToAllTransactions(categoryId))
            }
            DetailCategoryEvent.OnSeeAllSubCategoryClick -> {
                viewModelScope.launch {
                    sendEffect(
                        DetailCategoryEffect.NavigateToAllSubCategories(
                            categoryId,
                            state.value.dataState.dataOrNull?.category?.name ?: "",
                        ),
                    )
                }
            }
            is DetailCategoryEvent.OnSubCategoryClick -> {
                viewModelScope.launch {
                    sendEffect(
                        if (event.subCategoryId != null) {
                            NavigateToSubCategoryDetail(event.subCategoryId)
                        } else {
                            ShowSnackbar(
                                UiText.Raw("Sub Category ID is null"),
                            )
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
                            ShowSnackbar(
                                UiText.Raw("Transaction ID is null"),
                            )
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

            DetailCategoryEvent.OnAddNoteClick -> {
                viewModelScope.launch {

                }
            }

            DetailCategoryEvent.OnEditBudgetClick -> viewModelScope.launch {
                sendEffect(
                    DetailCategoryEffect.NavigateToEditBudget(categoryId = categoryId),
                )
            }

            // Fitur notifikasi belum ada — jangan TODO() (crash saat diklik),
            // beri tahu pengguna lewat snackbar yang kini benar-benar dirender.
            DetailCategoryEvent.OnSetNotificationClick -> viewModelScope.launch {
                sendEffect(
                    ShowSnackbar(
                        uiText(R.string.detail_category_notification_coming_soon),
                    ),
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadDetailCategory(categoryId: String) {
        viewModelScope.launch {
            combine(
                categoryUseCases.getCategoryById(id = categoryId),
                getSubCategoriesWithBudgetUseCase(categoryId),
                transactionRepository.getRecentTransactionsByCategory(5, categoryId),
            ) { category, budgetResult, transactions ->
                Triple(category, budgetResult, transactions)
            }
                    .catch { e -> updateState { it.copy(dataState = UiState.Error(e.message.toString())) } }
                    .collect { (category, budgetResult, transactions) ->
                        updateState { current ->
                            val existingData = current.dataState.dataOrNull
                            current.copy(
                                dataState = UiState.Success(
                                    DetailCategoryUiModel(
                                        // Budget induk (rollup: transaksi induk + sub) & sub
                                        // datang dari use case yang sama sehingga angkanya
                                        // selalu selaras — tidak lagi pipeline terpisah
                                        // yang bisa kalah cepat dengan pembentukan Success.
                                        category = category?.toUiModel(
                                            totalBudget = budgetResult.parentBudgetAmount,
                                            usedBudget = budgetResult.parentUsedAmount,
                                        ),
                                        subCategories = budgetResult.subCategories.map { detail ->
                                            detail.category.toUiModel(
                                                totalBudget = detail.totalBudget,
                                                usedBudget = detail.usedBudget,
                                            )
                                        },
                                        // Transaksi terakhir datang langsung dari combine —
                                        // bila dialirkan terpisah (onEach + guard Success),
                                        // emit pertama bisa kalah cepat dengan pembentukan
                                        // Success dan hilang permanen.
                                        recentTransactions = transactions.map { it.toItemUiModel() },
                                        // Tren mingguan flow-nya terpisah (observer state):
                                        // pertahankan agar re-emit kategori tidak mengosongkannya.
                                        weeklyData = existingData?.weeklyData ?: emptyList(),
                                        weeklyLabels = existingData?.weeklyLabels ?: emptyList(),
                                        // Info budget induk juga di-patch observer terpisah —
                                        // pertahankan agar re-emit Room tidak menghapusnya
                                        // (sama pola dengan tren mingguan di atas).
                                        parentInfo = existingData?.parentInfo,
                                    ),
                                ),
                            )
                        }
                    }
        }
    }

    /**
     * Anggaran **induk** untuk layar sub (pola: sub ngikut induk).
     *
     * - Kategori induk (`parentId == null`) → flow ini tidak pernah jalan,
     *   [ParentBudgetUiModel] tetap null dan layar memakai kartu anggarannya sendiri.
     * - Gagal memuat induk sengaja **diabaikan** (bukan `UiState.Error`): layar tetap utuh,
     *   hanya kartu anggaran induknya yang tidak tampil.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun handleParentBudgetObserver() {
        viewModelScope.launch {
            state
                    .mapNotNull { it.dataState.dataOrNull?.category?.parentId }
                    .distinctUntilChanged()
                    .flatMapLatest { parentId ->
                        combine(
                            categoryUseCases.getCategoryById(id = parentId),
                            getSubCategoriesWithBudgetUseCase(parentId),
                        ) { parent, result -> Triple(parentId, parent, result) }
                    }
                    .catch { /* abaikan — parentInfo tetap null */ }
                    .collect { (parentId, parent, result) ->
                        updateState { current ->
                            val existingData =
                                current.dataState.dataOrNull ?: return@updateState current
                            val thisDetail =
                                result.subCategories.firstOrNull { it.category.id == categoryId }

                            current.copy(
                                dataState = UiState.Success(
                                    existingData.copy(
                                        parentInfo = ParentBudgetUiModel(
                                            id = parentId,
                                            name = parent?.name ?: "",
                                            totalBudget = result.parentBudgetAmount,
                                            usedBudget = result.parentUsedAmount,
                                            thisUsedBudget = thisDetail?.usedBudget ?: 0L,
                                        ),
                                    ),
                                ),
                            )
                        }
                    }
        }
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
                            val existingData =
                                current.dataState.dataOrNull ?: return@updateState current
                            current.copy(
                                dataState = UiState.Success(
                                    existingData.copy(
                                        weeklyData = trend.data,
                                        weeklyLabels = trend.labels,
                                    ),
                                ),
                            )
                        }
                    }
        }
    }
}
