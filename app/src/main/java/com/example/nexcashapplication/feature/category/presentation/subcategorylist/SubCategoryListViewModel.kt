package com.example.nexcashapplication.feature.category.presentation.subcategorylist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.common.BaseViewModel
import com.example.nexcashapplication.core.i18n.toUiText
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.category.domain.usecase.GetSubCategoriesWithBudgetUseCase
import com.example.nexcashapplication.feature.category.presentation.mapper.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubCategoryListViewModel internal constructor(
    private val route: Route.CategoryRoute.SubCategoryList,
    private val getSubCategoriesWithBudgetUseCase: GetSubCategoriesWithBudgetUseCase,
) : BaseViewModel<SubCategoryListState, SubCategoryListEvent, SubCategoryListEffect>(initialState = SubCategoryListState()) {

    private val categoryId = route.categoryId

    @Inject constructor(
        getSubCategoriesWithBudgetUseCase: GetSubCategoriesWithBudgetUseCase,
        savedStateHandle: SavedStateHandle,
    ) : this(
        route = savedStateHandle.toRoute<Route.CategoryRoute.SubCategoryList>(),
        getSubCategoriesWithBudgetUseCase = getSubCategoriesWithBudgetUseCase,
    )

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

            is SubCategoryListEvent.OnRetryClick -> observerSubCategories()
        }
    }

    /**
     * Sub + budget lew [GetSubCategoriesWithBudgetUseCase] — sama dengan layar detail,
     * supaya kartu sub tidak lagi menampilkan `0% / Rp 0`.
     * Budget induk ikut dibawa untuk kontribusi sub yang belum punya budget sendiri.
     *
     * `catch` wajib: tanpanya kegagalan Room bocor ke `viewModelScope` = crash. Gagal baca
     * mengisi `error` dan menghentikan indikator supaya layar menampilkan pesan + tombol
     * coba lagi ([SubCategoryListEvent.OnRetryClick]) alih-alih daftar kosong diam.
     */
    private fun observerSubCategories() {
        updateState { it.copy(isLoading = true, error = null) }
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
                            error = null,
                        )
                    }
                }
                .catch { e ->
                    updateState {
                        it.copy(
                            isLoading = false,
                            error = e.toUiText(R.string.cat_load_failed),
                        )
                    }
                }
                .launchIn(viewModelScope)
    }
}
