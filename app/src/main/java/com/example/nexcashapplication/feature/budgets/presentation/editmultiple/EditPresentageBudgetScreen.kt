package com.example.nexcashapplication.feature.budgets.presentation.editmultiple

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.core.i18n.resolve
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.budgets.presentation.editmultiple.model.EditPresentageBudgetItemUiModel
import com.example.nexcashapplication.feature.category.presentation.main.AddCategoryButton
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import com.example.nexcashapplication.ui.theme.PrimaryCyan

@Composable
fun EditPresentageBudgetScreen(
    viewModel: EditPresentageBudgetViewModel = hiltViewModel(),
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                EditPresentageBudgetEffect.NavigateBack -> navController.popBackStack()

                EditPresentageBudgetEffect.NavigateToAddCategory ->
                    navController.navigate(Route.CategoryRoute.AddNew())

                is EditPresentageBudgetEffect.ShowSnackbar ->
                    snackbarHostState.showSnackbar(effect.message.resolve(context))
            }
        }
    }

    EditPresentageBudgetContent(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPresentageBudgetContent(
    state: EditPresentageBudgetState,
    onEvent: (EditPresentageBudgetEvent) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.budget_edit_allocations_title)) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(EditPresentageBudgetEvent.OnBackClick) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.budget_back_cd),
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { onEvent(EditPresentageBudgetEvent.OnSaveClick) },
                        enabled = !state.isSaving,
                    ) {
                        Text(
                            if (state.isSaving) {
                                stringResource(R.string.budget_saving_action)
                            } else {
                                stringResource(R.string.budget_save_action)
                            },
                        )
                    }
                },
            )
        },
//        bottomBar = {
//            EditAnggaranBottomBar(
//                isValid = state.isValid,
//                isSaving = state.isSaving,
//                onBatal = { onEvent(EditAnggaranEvent.OnBatalClick) },
//                onSimpan = { onEvent(EditAnggaranEvent.OnSimpanClick) }
//            )
//        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            item { BudgetSummaryRow(state.totalBudget, state.totalUsed, state.totalRemaining) }

            // Validasi alokasi (pengganti ValidasiAlokasiText yang dikomentari)
            item {
                Text(
                    text = if (state.isValid) {
                        stringResource(R.string.budget_allocation_ready, state.totalPercentage)
                    } else {
                        stringResource(
                            R.string.budget_allocation_invalid_hint,
                            state.totalPercentage,
                        )
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (state.isValid) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                )
            }

//            item {
//                DistribusiModeTabs(
//                    selected = state.distribusiMode,
//                    onSelect = { onEvent(EditAnggaranEvent.OnDistribusiModeChange(it)) }
//                )
//            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        stringResource(R.string.budget_expense_categories_title),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        stringResource(
                            R.string.budget_active_categories_count,
                            state.budgets.size,
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }

            items(state.budgets, key = { it.id }) { budget ->
                CategoryAllocationCard(
                    category = budget,
                    onSliderChange = { newPercent ->
                        onEvent(EditPresentageBudgetEvent.OnSliderChange(budget.id, newPercent))
                    },
                    onAmountChange = { newAmount ->
                        onEvent(EditPresentageBudgetEvent.OnAmountInputChange(budget.id, newAmount))
                    },
                    onDeleteClick = {
                        onEvent(EditPresentageBudgetEvent.OnDeleteBudgetClick(budget.id))
                    },
                )
            }

            item { AddCategoryButton { onEvent(EditPresentageBudgetEvent.OnAddCategoryClick) } }

//            state.projectedSavingsChangePercent?.let { percent ->
//                item {
//                    DampakPerubahanCard(percent) {
//                        onEvent(EditPresentageBudgetEvent.OnDampakPerubahanClick)
//                    }
//                }
//            }

//            item { ValidasiAlokasiText(isValid = state.isValid, total = state.totalPercentage) }
        }

        // Konfirmasi hapus alokasi per kategori (tutup seri, riwayat tetap)
        state.deleteConfirmationId?.let { deleteId ->
            val categoryName = state.budgets.firstOrNull { it.id == deleteId }
                    ?.name ?: stringResource(R.string.budget_this_category)
            AlertDialog(
                onDismissRequest = { onEvent(EditPresentageBudgetEvent.OnDismissDeleteClick) },
                title = { Text(stringResource(R.string.budget_delete_allocation_dialog_title)) },
                text = {
                    Text(
                        stringResource(
                            R.string.budget_delete_allocation_dialog_message,
                            categoryName,
                        ),
                    )
                },
                confirmButton = {
                    TextButton(onClick = { onEvent(EditPresentageBudgetEvent.OnConfirmDeleteClick) }) {
                        Text(
                            stringResource(R.string.budget_delete_action),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onEvent(EditPresentageBudgetEvent.OnDismissDeleteClick) }) {
                        Text(stringResource(R.string.budget_cancel_action))
                    }
                },
            )
        }
    }
}

@Composable
fun BudgetSummaryRow(budget: Long, used: Long, remaining: Long) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SummaryBudgetCard(
            stringResource(R.string.budget_summary_total),
            budget,
            modifier = Modifier.weight(1f),
        )
        SummaryBudgetCard(
            stringResource(R.string.budget_summary_used),
            used,
            modifier = Modifier.weight(1f),

        )
        SummaryBudgetCard(
            stringResource(R.string.budget_summary_remaining),
            remaining,
            modifier = Modifier.weight(1f),

        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryAllocationCard(
    category: EditPresentageBudgetItemUiModel,
    onSliderChange: (Int) -> Unit,
    onAmountChange: (Long) -> Unit,
    onDeleteClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
//    var localPercentage by remember(category.percentage) {
//        mutableFloatStateOf(category.percentage)
//    }

    Column(
        Modifier
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                )
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(category.name, style = MaterialTheme.typography.titleSmall)
                Text(
                    stringResource(R.string.budget_percentage_of_total, category.percentage),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Text(
                    text = category.totalBudget.formatCurrency(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            Spacer(Modifier.width(4.dp))
            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.budget_delete_allocation_cd, category.name),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Slider(
                modifier = Modifier.weight(1f),
                value = category.percentage,
                valueRange = 0f..100f,
//                onValueChange = { localPercentage = it },
                onValueChange = { onSliderChange(it.toInt()) },
                thumb = {
                    Box(
                        modifier = Modifier
                                .size(20.dp)
                                .border(
                                    width = 2.dp,
                                    color = PrimaryCyan,
                                    shape = CircleShape,
                                )
                                .background(Color.White, CircleShape),
                    )
                },
                track = { sliderState ->
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        colors = SliderDefaults.colors(
                            activeTrackColor = PrimaryCyan,
                            inactiveTrackColor = PrimaryCyan.copy(alpha = 0.2f),
                        ),
                        modifier = Modifier.height(8.dp), // default-nya ~4.dp
                    )
                },
            )
            Spacer(Modifier.width(8.dp))
            Text(
                stringResource(R.string.budget_percentage_value, category.percentage),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
fun SummaryBudgetCard(
    title: String,
    value: Long,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier
                .shadow(
                    elevation = 4.dp,
                    ambientColor = PrimaryCyan,
                    spotColor = PrimaryCyan,
                    shape = RoundedCornerShape(10.dp),
                )
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
        )
        Text(
            text = value.formatCurrency(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
fun RupiahTextField(value: Long, onValueChange: (Long) -> Unit) {
    TODO("Not yet implemented")
}


@Preview(showBackground = true)
@Composable
private fun EditPresentageBudgetContentPreview() {
    val dummyBudgets = listOf(
        EditPresentageBudgetItemUiModel(
            id = "1",
            name = "Makanan",
            percentage = 40f,
            color = Color(0xFFFF6B6B),
            totalBudget = 2_000_000L,
        ),
        EditPresentageBudgetItemUiModel(
            id = "2",
            name = "Transportasi",
            percentage = 25f,
            color = Color(0xFF4ECDC4),
            totalBudget = 1_250_000L,
        ),
        EditPresentageBudgetItemUiModel(
            id = "3",
            name = "Hiburan",
            percentage = 15f,
            color = Color(0xFFFFD93D),
            totalBudget = 750_000L,
        ),
        EditPresentageBudgetItemUiModel(
            id = "4",
            name = "Tabungan",
            percentage = 20f,
            color = Color(0xFF6C5CE7),
            totalBudget = 1_000_000L,
        ),
    )

    NexCashApplicationTheme {
        EditPresentageBudgetContent(
            state = EditPresentageBudgetState(
                budgetId = "budget_1",
                totalBudget = 5_000_000L,
                totalUsed = 3_200_000L,
                totalRemaining = 1_800_000L,
                budgets = dummyBudgets,
                showMonthPicker = false,
            ),
            onEvent = {},
        )
    }
}