package com.example.nexcashapplication.feature.category.presentation.delete

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.component.IconCircular
import com.example.nexcashapplication.core.domain.model.CategoryIcon
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.core.i18n.resolve
import com.example.nexcashapplication.feature.category.presentation.delete.model.CategoryDeleteItemUI
import com.example.nexcashapplication.feature.category.presentation.delete.model.DeleteOption
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import kotlinx.coroutines.flow.collectLatest

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.key
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.HorizontalDivider

@Composable
fun DeleteCategoryScreen(
    viewModel: DeleteCategoryViewModel = hiltViewModel(),
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val deleteSuccessText = stringResource(R.string.cat_delete_success)

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                DeleteCategoryEffect.DeleteSuccess -> {
                    Toast.makeText(context, deleteSuccessText, Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
                DeleteCategoryEffect.NavigateBack -> {
                    navController.popBackStack()
                }
                is DeleteCategoryEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message.resolve(context))
                }
            }
        }
    }

    DeleteCategoryContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteCategoryContent(
    state: DeleteCategoryState,
    snackbarHostState: SnackbarHostState,
    onEvent: (DeleteCategoryEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.cat_delete_title),
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(DeleteCategoryEvent.Cancel) }) {
                        Icon(
                            Icons.Filled.ArrowBackIosNew,
                            contentDescription = stringResource(R.string.cat_back),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Column {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Box(
                    modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                ) {
                    Button(
                        onClick = { onEvent(DeleteCategoryEvent.ConfirmDelete) },
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                        enabled = state.isConfirmEnabled,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                            disabledContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            stringResource(R.string.cat_delete_action),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Spacer(modifier = Modifier.fillMaxSize().padding(padding))
        } else {
            Column(
                modifier = Modifier
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(24.dp))

                IconCircular(
                    icon = Icons.Default.Warning,
                    iconColor = MaterialTheme.colorScheme.error,
                    backgroundColor = MaterialTheme.colorScheme.errorContainer,
                    iconSize = 72.dp,
                    iconToCountainerRatio = 2,
                )

                Spacer(Modifier.height(16.dp))
                Text(
                    stringResource(R.string.cat_delete_confirm_heading),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    stringResource(
                        R.string.cat_delete_description_format,
                        state.categoryName,
                        state.transactionCount,
                    ),
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(20.dp))

                OptionCard(
                    icon = Icons.Filled.Delete,
                    title = stringResource(R.string.cat_option_delete_all_title),
                    description = stringResource(R.string.cat_option_delete_all_desc),
                    selected = state.selectedOption == DeleteOption.DELETE_ALL_TRANSACTIONS,
                    onClick = { onEvent(DeleteCategoryEvent.SelectOption(DeleteOption.DELETE_ALL_TRANSACTIONS)) },
                )

                Spacer(Modifier.height(10.dp))

                OptionCard(
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    title = stringResource(R.string.cat_option_move_title),
                    description = stringResource(R.string.cat_option_move_desc),
                    selected = state.selectedOption == DeleteOption.MOVE_TO_CATEGORY,
                    onClick = { onEvent(DeleteCategoryEvent.SelectOption(DeleteOption.MOVE_TO_CATEGORY)) },
                )

                if (state.selectedOption == DeleteOption.MOVE_TO_CATEGORY) {
                    Spacer(Modifier.height(10.dp))
                    CategoryDropdown(
                        modifier = Modifier.padding(start = 28.dp),
                        categories = state.availableCategories,
                        selected = state.targetCategory,
                        expanded = state.isDropdownExpanded,
                        onExpandedChange = { onEvent(DeleteCategoryEvent.ToggleDropdown) },
                        onSelect = { onEvent(DeleteCategoryEvent.SelectTargetCategory(it)) },
                    )
                }

                Spacer(Modifier.height(10.dp))

                OptionCard(
                    icon = Icons.Filled.HelpOutline,
                    title = stringResource(R.string.cat_option_uncategorized_title),
                    description = stringResource(R.string.cat_option_uncategorized_desc),
                    selected = state.selectedOption == DeleteOption.UNCATEGORIZED,
                    onClick = { onEvent(DeleteCategoryEvent.SelectOption(DeleteOption.UNCATEGORIZED)) },
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun OptionCard(
    icon: ImageVector,
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(10.dp))

            Column {
                Row {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier
                                .size(18.dp)
                                .padding(top = 2.dp),
                        tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
                Spacer(Modifier.height(2.dp))
                Text(description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    categories: List<CategoryDeleteItemUI>,
    selected: CategoryDeleteItemUI?,
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    onSelect: (CategoryDeleteItemUI) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pickTargetLabel = stringResource(R.string.cat_pick_target_category)
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { onExpandedChange() },
    ) {
        OutlinedTextField(
            value = selected?.name ?: pickTargetLabel,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(pickTargetLabel) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            ),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange() },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            categories.forEach { category ->
                key(category.id) {
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = { onSelect(category) },
                        leadingIcon = {
                            Icon(
                                imageVector = category.icon.icon,
                                contentDescription = null,
                                tint = category.iconColor,
                            )
                        },
                        trailingIcon = {
                            if (selected?.id == category.id) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeleteCategoryPreview() {
    val sampleCategories = listOf(
        CategoryDeleteItemUI(
            id = "1L", name = "Kebutuhan Rumah", icon = CategoryIcon(id = "", Icons.Filled.Home),
            iconColor = Color.Blue,
        ),
        CategoryDeleteItemUI(
            id = "2L",
            name = "Makanan & Minum",
            icon = CategoryIcon(id = "", Icons.Filled.Home),
            iconColor = Color.Blue,

            ),
        CategoryDeleteItemUI(
            id = "3L",
            name = "Transportasi",
            icon = CategoryIcon(id = "", Icons.Filled.Home),
            iconColor = Color.Blue,

            ),
        CategoryDeleteItemUI(
            id = "4L",
            name = "Belanja Umum",
            icon = CategoryIcon(id = "", Icons.Filled.Home),
            iconColor = Color.Blue,

            ),
    )

    val previewState = DeleteCategoryState(
        categoryName = "Hiburan",
        transactionCount = 12,
        selectedOption = DeleteOption.MOVE_TO_CATEGORY,
        availableCategories = sampleCategories,
        targetCategory = sampleCategories.first(),
        isDropdownExpanded = false,
    )

    NexCashApplicationTheme {
        DeleteCategoryContent(
            state = previewState,
            snackbarHostState = remember { SnackbarHostState() },
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Delete All - No Target Selected")
@Composable
private fun DeleteCategoryDeleteAllPreview() {
    NexCashApplicationTheme {
        DeleteCategoryContent(
            state = DeleteCategoryState(
                categoryName = "Hiburan",
                transactionCount = 12,
                selectedOption = DeleteOption.DELETE_ALL_TRANSACTIONS,
                availableCategories = emptyList(),
                targetCategory = null,
                isDropdownExpanded = false,
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onEvent = {},
        )
    }
}

