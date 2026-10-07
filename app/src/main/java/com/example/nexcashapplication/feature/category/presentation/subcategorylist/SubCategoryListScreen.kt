package com.example.nexcashapplication.feature.category.presentation.subcategorylist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.domain.model.CategoryIcon
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.domain.model.TypeCategory.EXPENSE
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.category.presentation.detail.SubcategoryGrid
import com.example.nexcashapplication.feature.category.presentation.shared.model.CategoryUiItemModel
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme

// SubCategoryListScreen.kt — stateful, TIDAK di-preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubCategoryListScreen(
    navHostController: NavHostController,
    viewModel: SubCategoryListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SubCategoryListEffect.NavigateBack -> navHostController.popBackStack()
                is SubCategoryListEffect.NavigateToSubCategoryDetail -> {
                    // Sub punya layar detail sendiri (reuse CategoryRoute.Detail);
                    // sebelumnya salah navigasi ke SubCategoryList tanpa args.
                    navHostController.navigate(
                        Route.CategoryRoute.Detail(effect.subCategoryId),
                    )
                }
            }
        }
    }

    SubCategoryListContent(
        state = state,
        onEvent = viewModel::onEvent,
    )
}

// SubCategoryListContent.kt — stateless, INI yang di-preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubCategoryListContent(
    state: SubCategoryListState,
    onEvent: (SubCategoryListEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            R.string.cat_subcategory_list_title,
                            state.parentCategoryName,
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(SubCategoryListEvent.OnBackClick) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cat_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
        ) {
            SubcategoryGrid(
                subcategories = state.subCategories,
                parentTotalBudget = state.parentTotalBudget,
                parentName = state.parentCategoryName,
                onSubcategoryClick = { id ->
                    onEvent(SubCategoryListEvent.OnSubCategoryClick(id))
                },
            )
        }
    }
}
val dummySubCategories = listOf(
    CategoryUiItemModel(
        id = "sub-1",
        parentId = "parent-transportasi",
        name = "Bensin",
        icon = CategoryIcon(
            id = "fuel",
            icon = Icons.Default.LocalGasStation,
        ),
        color = Color(0xFF3B82F6),
        totalBudget = 750_000L,
        usedBudget = 450_000L,   // 60%
        typeCategory = EXPENSE, // <- masih tebakan, lihat catatan di bawah
    ),
    CategoryUiItemModel(
        id = "sub-2",
        parentId = "parent-transportasi",
        name = "Parkir",
        icon = CategoryIcon(
            id = "parking",
            icon = Icons.Default.LocalParking,
        ),
        color = Color(0xFF6366F1),
        totalBudget = 300_000L,
        usedBudget = 120_000L,   // 40%
        typeCategory = EXPENSE,
    ),
    CategoryUiItemModel(
        id = "sub-3",
        parentId = "parent-transportasi",
        name = "Ojek Online",
        icon = CategoryIcon(
            id = "bike",
            icon = Icons.AutoMirrored.Filled.DirectionsBike,
        ),
        color = Color(0xFF06B6D4),
        totalBudget = 447_058L,
        usedBudget = 380_000L,   // ~85%
        typeCategory = EXPENSE,
    ),
    CategoryUiItemModel(
        id = "sub-4",
        parentId = "parent-transportasi",
        name = "Tol & Tiket",
        icon = CategoryIcon(
            id = "toll_card",
            icon = Icons.Default.CreditCard,
        ),
        color = Color(0xFF8B5CF6),
        totalBudget = 400_000L,
        usedBudget = 100_000L,   // 25%
        typeCategory = EXPENSE,
    ),
)

// Preview-nya, taruh di file yang sama dengan Content atau file terpisah *Preview.kt
@Preview(showBackground = true)
@Composable
private fun SubCategoryListContentPreview() {
    NexCashApplicationTheme { // ganti sesuai nama theme project kamu
        SubCategoryListContent(
            state = SubCategoryListState(
                isLoading = false,
                subCategories = dummySubCategories,
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SubCategoryListContentLoadingPreview() {
    NexCashApplicationTheme {
        SubCategoryListContent(
            state = SubCategoryListState(isLoading = true),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SubCategoryListContentEmptyPreview() {
    NexCashApplicationTheme {
        SubCategoryListContent(
            state = SubCategoryListState(isLoading = false, subCategories = emptyList()),
            onEvent = {},
        )
    }
}