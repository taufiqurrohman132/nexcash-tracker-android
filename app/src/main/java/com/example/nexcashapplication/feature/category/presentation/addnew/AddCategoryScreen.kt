package com.example.nexcashapplication.feature.category.presentation.addnew

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.component.AppButton
import com.example.nexcashapplication.core.component.FormField
import com.example.nexcashapplication.core.component.IconRounded
import com.example.nexcashapplication.core.domain.model.CategoryIcon
import com.example.nexcashapplication.core.domain.model.CategoryIconGroup
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.category.presentation.addnew.constant.CategoryIcons
import com.example.nexcashapplication.feature.category.presentation.addnew.constant.nameResId
import com.example.nexcashapplication.feature.category.presentation.main.CategoryEffect
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import com.example.nexcashapplication.ui.theme.TextSecondary
import com.example.nexcashapplication.ui.theme.TextTertiary
import io.mhssn.colorpicker.ColorPickerDialog
import io.mhssn.colorpicker.ColorPickerType


@Composable
fun AddCategoryScreen(
    viewModel: AddCategoryViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { navigate ->
            when (navigate) {
                is CategoryEffect.NavigateBack -> navController.popBackStack()
                else -> {}
            }
        }
    }
    AddCategoryContent(state, viewModel::onEvent)

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AddCategoryContent(
    state: AddCategoryState,
    onEvent: (AddCategoryEvent) -> Unit,
    modifier: Modifier = Modifier,
) {

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.cat_add_title),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(AddCategoryEvent.OnBackClicked) }) {
                        Icon(
                            Icons.Default.KeyboardArrowLeft,
                            contentDescription = stringResource(R.string.cat_back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Spacer(Modifier.height(4.dp))

            state.parentCategory?.let { parent ->
                ParentCategoryBanner(
                    icon = parent.icon.icon,
                    iconColor = parent.color,
                    bannerColor = parent.color.copy(alpha = 0.2f),
                    nameCategory = parent.name,
                )
            }

            // nama kategori
            FormField(
                title = stringResource(R.string.cat_name_label),
            ) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { onEvent(AddCategoryEvent.OnNameChanged(it)) },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.cat_name_placeholder),
                            color = TextTertiary,
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryCyan,
                        unfocusedBorderColor = BorderGray,
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                            .height(50.dp)
                            .fillMaxWidth(),
                )
            }

            // Tipe Kategori
            FormField(title = stringResource(R.string.cat_type_label)) {

                TipeKategoriToggle(
                    selected = state.typeCategory,
                    onSelectedCategory = { onEvent(AddCategoryEvent.OnTypeSelected(it)) },
                )
            }


            ColorSwatchRow(
                colors = state.recentColors,
                selectedColor = state.selectedColor,
                onAddColorClick = { onEvent(AddCategoryEvent.OnAddColorClicked) },
                onColorSelected = { onEvent(AddCategoryEvent.OnColorSelected(it)) },
            )


            IconPreviewRow(
                selectedIcon = state.selectedIcon,
                recentIcons = state.recentIcons,
                onIconClick = { onEvent(AddCategoryEvent.OnChangeIcon(it)) },
                onMenuIconClick = { showBottomSheet = true },
            )


            FormField(stringResource(R.string.cat_note_label)) {
                OutlinedTextField(
                    value = state.note,
                    onValueChange = { onEvent(AddCategoryEvent.OnNotesChanged(it)) },
                    placeholder = {
                        Text(
                            stringResource(R.string.cat_note_placeholder),
                            color = TextTertiary,
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryCyan,
                        unfocusedBorderColor = BorderGray,
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                )
            }

            Spacer(Modifier.height(4.dp))

            // Tombol Aksi
            AppButton(
                title = stringResource(R.string.cat_finish_action),
                onClick = {
                    onEvent(AddCategoryEvent.OnSaveClicked)
                },
            )

            AppButton(
                title = stringResource(R.string.cat_cancel_back_action),
                textColor = TextSecondary,
                backgroundColor = Color.White,
                onClick = { onEvent(AddCategoryEvent.OnBackClicked) },
            )

            Spacer(modifier = Modifier.height(24.dp))

        }

        // color picker
        ColorPickerDialog(
            show = state.showColorPalletDialog,
            type = ColorPickerType.Ring(
                ringWidth = 10.dp,
                previewRadius = 80.dp,
                showDarknessBar = false,
                showLightnessBar = false,
                showAlphaBar = false,
                showColorPreview = true,
            ),
            properties = DialogProperties(),
            onDismissRequest = {
                onEvent(AddCategoryEvent.OnColorPickerDismiss)
            },
            onPickedColor = {
                onEvent(AddCategoryEvent.OnColorSelected(it))
            },

            )

        if (showBottomSheet) {
            ModalBottomSheet(
                dragHandle = {
                    BottomSheetDefaults.DragHandle(
                        width = 40.dp,
                    )
                },
                containerColor = Color.White,
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
            ) {
                MenuIcons(
                    selectedIcon = state.selectedIcon,
                    groups = CategoryIcons.allGroups,
                    onIconSelected = {
                        onEvent(AddCategoryEvent.OnChangeIcon(it))
                        showBottomSheet = false
                    },
                )
            }
        }
    }
}


@Composable
fun ParentCategoryBanner(
    iconColor: Color,
    bannerColor: Color,
    icon: ImageVector,
    nameCategory: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(bannerColor),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconRounded(
                iconColor = Color.White,
                backgroundColor = iconColor,
                imageVector = icon,
            )

            Column(

            ) {
                Text(
                    text = stringResource(R.string.cat_subcategory_of),
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary,
                )
                Text(
                    text = nameCategory,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

        }
    }
}

@Composable
fun ColorSwatchRow(
    colors: List<Color>,
    selectedColor: Color,
    onAddColorClick: () -> Unit,
    onColorSelected: (Color) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                        .size(46.dp)
                        .then(
                            if (color == selectedColor) {
                                Modifier.border(2.dp, Color.Black, CircleShape)
                            } else Modifier,
                        )
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .clickable { onColorSelected(color) },
            )
        }

        AddColorButton(
            onClick = {
                onAddColorClick()
            },
        )
    }
}


@Composable
fun TipeKategoriToggle(
    selected: TypeCategory,
    onSelectedCategory: (TypeCategory) -> Unit,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TypeCategory.entries.forEach { typeCategory ->
            val isSelected = typeCategory == selected

            FilterChip(
                modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 40.dp),
                selected = isSelected,
                onClick = { onSelectedCategory(typeCategory) },
                label = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = stringResource(typeCategory.labelResId),
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    )
                },
                shape = RoundedCornerShape(50), // full rounded / pill shape
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White,
                    labelColor = Color.Black,
                    selectedContainerColor = PrimaryCyan.copy(alpha = 0.12f),
                    selectedLabelColor = PrimaryCyan,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = BorderGray,
                    selectedBorderColor = PrimaryCyan,
                    borderWidth = 1.dp,
                    selectedBorderWidth = 1.dp,
                ),
            )
        }
    }
}

@Composable
fun IconPreviewRow(
    selectedIcon: CategoryIcon?,
    recentIcons: List<CategoryIcon>,
    onIconClick: (CategoryIcon) -> Unit,
    onMenuIconClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
        ) {
            recentIcons.forEach { icon ->
                IconChip(
                    icon = icon.icon,
                    isSelected = icon == selectedIcon,
                    onClick = {
                        onIconClick(icon)
                    },
                )
            }
        }

        IconChip(
            modifier = Modifier.shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false,
                ambientColor = PrimaryCyan,
                spotColor = PrimaryCyan,
            ),
            icon = Icons.Default.GridView,
            isSelected = false,
            onClick = { onMenuIconClick() },
            contentDescription = stringResource(R.string.cat_see_all_icons_desc),
            iconColor = Color.White,
            backgroundColor = PrimaryCyan,
        )
    }
}

@Composable
fun IconChip(
    icon: ImageVector,
    isSelected: Boolean,
    iconColor: Color? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    contentDescription: String? = null,
) {
    Box(
        modifier = modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    backgroundColor ?: run {
                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                        else Color.White
                    },
                )
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else BorderGray,
                    shape = RoundedCornerShape(16.dp),
                )
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconColor ?: run {
                if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF666666)
            },
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
fun MenuIcons(
    selectedIcon: CategoryIcon?,
    groups: List<CategoryIconGroup>,
    onIconSelected: (CategoryIcon) -> Unit,
    modifier: Modifier = Modifier,
) {
    var hasScrolledThisGesture by remember { mutableStateOf(false) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (consumed.y != 0f) {
                    hasScrolledThisGesture = true
                }
                // cuma "telen" sisa delta kalau gesture ini SEBELUMNYA sempat scroll
                // (berarti baru mentok di tengah jalan) -> jangan bocor ke sheet
                return if (hasScrolledThisGesture && available.y != 0f) {
                    available
                } else {
                    Offset.Zero
                }
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                // gesture selesai (jari lepas) -> reset buat gesture berikutnya
                hasScrolledThisGesture = false
                return Velocity.Zero
            }
        }
    }
    Column(
        modifier = modifier
                .fillMaxWidth()
                .nestedScroll(nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
    ) {
        groups.forEach { group ->
            Text(
                text = stringResource(group.nameResId),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(vertical = 8.dp),
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                group.icons.forEach { icon ->
                    IconChip(
                        icon = icon.icon,
                        isSelected = selectedIcon == icon,
                        onClick = { onIconSelected(icon) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AddColorButton(onClick: () -> Unit) {
    val ringColors = listOf(
        Color(0xFF22C55E), // hijau
        Color(0xFF06B6D4), // cyan
        Color(0xFF3B82F6), // biru
        Color(0xFF8B5CF6), // ungu
        Color(0xFFEC4899), // pink
        Color(0xFFF59E0B), // oranye/kuning
        Color(0xFF22C55E),  // balik ke hijau biar nyambung
    )

    Box(
        modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Brush.sweepGradient(ringColors))
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                    .matchParentSize() // makin kecil dari parent = makin tebel ring-nya
                    .padding(6.dp)
                    .clip(CircleShape)
                    .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.cat_add_color_desc),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Preview(showBackground = true, name = "Default")
@Composable
fun AddCategoryContentPreview() {
    AddCategoryContent(
        state = AddCategoryState(
            name = "Makan & Minum",
            typeCategory = TypeCategory.EXPENSE,
            selectedColor = Color(0xFF4CAF50),
            selectedIcon = CategoryIcons.recentIcons.first(),
            note = "Kebutuhan harian",
            recentColors = listOf(
                Color(0xFF4CAF50),
                Color(0xFF2196F3),
                Color(0xFFF44336),
            ),
        ),
        onEvent = {},
    )
}

@Preview(showBackground = true, name = "Empty / Mode Tambah Baru")
@Composable
fun AddCategoryContentEmptyPreview() {
    AddCategoryContent(
        state = AddCategoryState(),
        onEvent = {},
    )
}

@Preview(showBackground = true, name = "Mode Edit")
@Composable
fun AddCategoryContentEditPreview() {
    AddCategoryContent(
        state = AddCategoryState(
            id = "cat-001", // ada id = mode edit
            name = "Transportasi",
            typeCategory = TypeCategory.EXPENSE,
            selectedColor = Color(0xFF2196F3),
            note = "Bensin & parkir",
        ),
        onEvent = {},
    )
}

@Preview(showBackground = true, name = "Add - Expense (Default)")
@Composable
private fun AddCategoryContentDefaultPreview() {
    NexCashApplicationTheme {
        AddCategoryContent(
            state = AddCategoryState(),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Add - Income Type")
@Composable
private fun AddCategoryContentIncomePreview() {
    NexCashApplicationTheme {
        AddCategoryContent(
            state = AddCategoryState(
                name = "Gaji",
                typeCategory = TypeCategory.INCOME,
                selectedColor = Color(0xFF9C27B0),
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Add - SubCategory (with Parent)")
@Composable
private fun AddCategoryContentSubCategoryPreview() {
    NexCashApplicationTheme {
        AddCategoryContent(
            state = AddCategoryState(
                name = "Bensin",
                typeCategory = TypeCategory.EXPENSE,
                parentCategory = ParentCategoryUi(
                    id = "id_transportasi",
                    name = "Transportasi",
                    icon = CategoryIcons.lainnya[0], // sesuaikan sama enum/sealed class icon kamu
                    color = PrimaryCyan,
                ),
                selectedColor = Color(0xFF3B82F6),
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Edit - Existing Category")
@Composable
private fun AddCategoryContentEdittPreview() {
    NexCashApplicationTheme {
        AddCategoryContent(
            state = AddCategoryState(
                id = "cat_001",
                name = "Makanan",
                typeCategory = TypeCategory.EXPENSE,
                selectedColor = Color(0xFFEF4444),
                note = "Kebutuhan makan sehari-hari",
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Add - No Budget Limit")
@Composable
private fun AddCategoryContentNoBudgetPreview() {
    NexCashApplicationTheme {
        AddCategoryContent(
            state = AddCategoryState(
                name = "Hiburan",
                typeCategory = TypeCategory.EXPENSE,
                selectedColor = Color(0xFFF59E0B),
            ),
            onEvent = {},
        )
    }
}


