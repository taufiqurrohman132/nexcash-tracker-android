package com.example.nexcashapplication.feature.category.presentation.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.category.presentation.main.model.CategoryItem
import com.example.nexcashapplication.feature.category.presentation.navigation.CategoryNavigationEvent
import com.example.nexcashapplication.feature.category.presentation.navigation.CategoryNavigationEvent.NavigateToDetail
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {
    private var _state = MutableStateFlow(
        CategoryState(
            categoryList = dummyKategori,
            totalAnggaran = dummyKategori.sumOf { it.budget },
            sisaAnggaran = dummyKategori.sumOf { it.budget - it.terpakai },
        ),
    )
    val state: StateFlow<CategoryState> = _state.asStateFlow()

    private var _navigationEvent = Channel<CategoryNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

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
                    _navigationEvent.send(CategoryNavigationEvent.NavigateToAddCategory)
                }
            }

            is CategoryEvent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = event.query) }
            }

            is CategoryEvent.OnTabCategoryClick -> {
                _state.update {
                    it.copy(
                        selectedTabCategory = event.type,
                    )
                }
            }

            is CategoryEvent.OnPageChanged -> {
                val type = TypeCategory.entries.getOrNull(event.index) ?: TypeCategory.EXPANSE
                _state.update { it.copy(selectedTabCategory = type) }
            }
        }
    }

    companion object {
        val dummyKategori = listOf(
            CategoryItem(
                1,
                "Makanan",
                TypeCategory.EXPANSE,
                Icons.Default.ShoppingCart,
                Color(0xFFFFE0CC),
                2_150_000,
                3_000_000,
            ),
            CategoryItem(
                2,
                "Transportasi",
                TypeCategory.INCOME,
                Icons.Default.LocationOn,
                Color(0xFFD9E4FF),
                850_000,
                1_000_000,
            ),
            CategoryItem(
                3,
                "Belanja",
                TypeCategory.EXPANSE,
                Icons.Default.Star,
                Color(0xFFFFD9E8),
                1_450_000,
                1_200_000,
            ),
            CategoryItem(
                4,
                "Kesehatan",
                TypeCategory.INCOME,
                Icons.Default.Favorite,
                Color(0xFFD4F5E0),
                300_000,
                500_000,
            ),
            CategoryItem(
                5,
                "Tagihan",
                TypeCategory.EXPANSE,
                Icons.Default.Warning,
                Color(0xFFFFF3D0),
                1_200_000,
                1_500_000,
            ),
            CategoryItem(
                6,
                "Hiburan",
                TypeCategory.INCOME,
                Icons.Default.PlayArrow,
                Color(0xFFEBDCFF),
                450_000,
                800_000,
            ),
            CategoryItem(
                7,
                "Investasi",
                TypeCategory.EXPANSE,
                Icons.Default.Info,
                Color(0xFFD4EAFF),
                2_000_000,
                2_000_000,
            ),
            CategoryItem(
                8,
                "Lainnya",
                TypeCategory.INCOME,
                Icons.Default.MoreVert,
                Color(0xFFEDEDED),
                120_000,
                300_000,
            ),
        )
    }
}
