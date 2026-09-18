package com.example.nexcashapplication.feature.category.presentation.detail;

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.nexcashapplication.core.component.FormField
import com.example.nexcashapplication.core.component.TransactionItem
import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.extension.formatCompact
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.core.navigations.Route.CategoryRoute.AddNew
import com.example.nexcashapplication.core.navigations.Route.CategoryRoute.Delete
import com.example.nexcashapplication.core.ui.dataOrNull
import com.example.nexcashapplication.feature.category.presentation.addnew.constant.CategoryIcons
import com.example.nexcashapplication.feature.category.presentation.detail.model.DetailCategoryUiModel
import com.example.nexcashapplication.feature.category.presentation.shared.model.CategoryUiItemModel
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import com.example.nexcashapplication.ui.theme.SecondaryDeepPurple
import com.example.nexcashapplication.ui.theme.TextTertiary
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent

val dummyTransactions = listOf(
    TransactionItemUiModel(
        id = "1",
        name = "Gaji Bulanan",
        time = "08:00",
        amount = 8_500_000,
        typeTransaction = TransactionType.INCOME,
        category = "Gaji",
        account = "BCA",
        icon = Icons.Default.AccountBalanceWallet,
        iconColor = Color.Black,
    ),
    TransactionItemUiModel(
        id = "2",
        name = "Makan Siang",
        time = "12:30",
        amount = 35_000,
        typeTransaction = TransactionType.INCOME,
        category = "Makanan & Minuman",
        account = "Cash",
        icon = Icons.Default.Restaurant,
        iconColor = Color.Black,
    ),
    TransactionItemUiModel(
        id = "3",
        name = "Langganan Netflix",
        time = "10:15",
        amount = 54_000,
        typeTransaction = TransactionType.INCOME,
        category = "Hiburan",
        account = "Gopay",
        icon = Icons.Default.Movie,
        iconColor = Color.Black,
    ),
    TransactionItemUiModel(
        id = "4",
        name = "Bensin Motor",
        time = "07:45",
        amount = 25_000,
        typeTransaction = TransactionType.INCOME,
        category = "Transportasi",
        account = "Cash",
        icon = Icons.Default.LocalGasStation,
        iconColor = Color.Black,
    ),
    TransactionItemUiModel(
        id = "5",
        name = "Freelance Project",
        time = "16:20",
        amount = 1_200_000,
        typeTransaction = TransactionType.INCOME,
        category = "Freelance",
        account = "BCA",
        icon = Icons.Default.Work,
        iconColor = Color.Black,
    ),
    TransactionItemUiModel(
        id = "6",
        name = "Belanja Bulanan",
        time = "19:00",
        amount = 450_000,
        typeTransaction = TransactionType.INCOME,
        category = "Belanja",
        account = "Gopay",
        icon = Icons.Default.ShoppingCart,
        iconColor = Color.Black,
    ),
    TransactionItemUiModel(
        id = "7",
        name = "Tagihan Listrik",
        time = "09:10",
        amount = 180_000,
        typeTransaction = TransactionType.INCOME,
        category = "Tagihan",
        account = "BCA",
        icon = Icons.Default.Bolt,
        iconColor = Color.Black,
    ),
    TransactionItemUiModel(
        id = "8",
        name = "Transfer dari Teman",
        time = "14:05",
        amount = 200_000,
        typeTransaction = TransactionType.INCOME,
        category = "Tanpa Kategori",
        account = "Akun Dihapus",
        icon = Icons.Default.Category,
        iconColor = Color.Black,
    ),
)

@Composable
fun DetailCategoryScreen(
    viewModel: DetailCategoryViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                DetailCategoryEffect.NavigateBack -> navController.popBackStack()
                is DetailCategoryEffect.NavigateToAddSubCategory -> navController.navigate(
                    AddNew(parentId = effect.parentId),
                )

                is DetailCategoryEffect.NavigateToAddTransaction -> {
                    navController.navigate(Route.TransactionRoute.AddEdit)
                }

                is DetailCategoryEffect.NavigateToAllSubCategories -> {
                    navController.navigate(
                        Route.CategoryRoute.SubCategoryList(
                            effect.categoryId,
                            effect.categoryName,
                        ),
                    )
                }

                is DetailCategoryEffect.NavigateToEditCategory -> navController.navigate(
                    AddNew(effect.categoryId),
                )

                is DetailCategoryEffect.NavigateToSubCategoryDetail -> navController.navigate(
                    Route.CategoryRoute.Detail(effect.subCategoryId),
                )

                is DetailCategoryEffect.NavigateToSubCategoryManagement -> {
                    // Navigate to management
                }

                is DetailCategoryEffect.NavigateToTransactionDetail -> navController.navigate(
                    Route.TransactionRoute.Detail(effect.transactionId),
                )

                DetailCategoryEffect.ShowMenuBottomSheet -> {
                    // Show sheet
                }

                is DetailCategoryEffect.ShowSnackbar -> {
                    // Show snackbar
                }

                is DetailCategoryEffect.NavigateToDeleteCategory -> navController.navigate(
                    Delete(
                        categoryId = effect.categoryId,
                        typeCategory = effect.typeCategory,
                    ),
                )

                is DetailCategoryEffect.NavigateToEditBudget -> {
                    navController.navigate(
                        Route.BudgetRoute.AddEdit(categoryId = effect.categoryId),
                    )
                }
            }
        }
    }

    DetailCategoryContent(
        state = state.dataState.dataOrNull ?: DetailCategoryUiModel(null),
        onEvent = viewModel::onEvent,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailCategoryContent(
    state: DetailCategoryUiModel,
    onEvent: (DetailCategoryEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showActionMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        state.category?.icon?.icon?.let {
                            Icon(
                                imageVector = it,
                                contentDescription = null,
                                tint = state.category.color,
                                modifier = Modifier
                                        .padding(end = 8.dp)
                                        .size(20.dp),
                            )
                        }
                        Text(
                            text = state.category?.name ?: "Detail",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(DetailCategoryEvent.OnBackClick) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showActionMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }

                    DropdownMenu(
                        expanded = showActionMenu,
                        onDismissRequest = { showActionMenu = false },
                        modifier = Modifier.background(Color.White),
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Category") },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            },
                            onClick = {
                                showActionMenu = false
                                onEvent(DetailCategoryEvent.OnEditClick)
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Hapus Category") },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            },
                            onClick = {
                                showActionMenu = false
                                onEvent(DetailCategoryEvent.OnDeleteClick)
                            },
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            if (state.category?.parentId == null) {
                ExtendedFloatingActionButton(
                    shape = CircleShape,
                    onClick = { onEvent(DetailCategoryEvent.OnAddSubCategoryClick) },
                    containerColor = PrimaryCyan,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            tint = Color.White,
                            contentDescription = null, // udah ada text di sebelahnya
                        )
                    },
                    text = {
                        Text(
                            text = "Subkategori",
                            color = Color.White,
                        )
                    },
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
//            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(Modifier.height(4.dp)) // pengganti contentPadding top

            BudgetSummaryCard(
                totalBudget = state.category?.totalBudget?.formatCurrency() ?: "0",
                used = state.category?.usedBudget?.formatCurrency() ?: "0",
                remaining = state.category?.remainingBudget?.formatCurrency() ?: "0",
                usedPersentage = state.category?.usedPercentage ?: 0,
            )
            Spacer(Modifier.height(16.dp))

            QuickActionsRow(
                onEditBudgetClick = { onEvent(DetailCategoryEvent.OnEditBudgetClick) },
                onSetNotificationClick = { onEvent(DetailCategoryEvent.OnSetNotificationClick) },
                onAddNoteClick = { onEvent(DetailCategoryEvent.OnAddNoteClick) },
            )


            FormField(
                title = "Tren Pengeluaran",
                actionText = "7 Hari Terakhir",
                onActionClick = { },
            ) {
                WeeklyTrendChart(
                    data = state.weeklyData,
                    labels = state.weeklyLabels,
                )
            }

            if (state.category?.parentId == null && state.subCategories.isNotEmpty()) {
                FormField(
                    title = "Sub Kategori",
                    actionText = "Lihat Semua",
                    onActionClick = { onEvent(DetailCategoryEvent.OnSeeAllSubCategoryClick) },
                ) {
                    SubcategoryGrid(
                        subcategories = state.subCategories,
                        onSubcategoryClick = { id ->
                            onEvent(
                                DetailCategoryEvent.OnSubCategoryClick(
                                    id,
                                ),
                            )
                        },
                    )
                }
            }


            FormField(
                title = "Transaksi Terakhir",
                icon = Icons.Default.Replay,
                actionText = "Lihat Semua",
                onActionClick = { onEvent(DetailCategoryEvent.OnLoadMoreTransactionsClick) },
            )

            TransactionListCard(
                transactions = state.recentTransactions,
                onItemClick = { id -> onEvent(DetailCategoryEvent.OnTransactionClick(id)) },
            )

            Spacer(Modifier.height(16.dp)) // pengganti contentPadding bottom
        }
    }
}


@Composable
fun TransactionListCard(
    transactions: List<TransactionItemUiModel>,
    onItemClick: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderGray),
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            transactions.forEachIndexed { index, transactionUi ->
                TransactionItem(
                    transaction = transactionUi,
                    onClick = { onItemClick(transactionUi.id ?: "") },
                )
                if (index < transactions.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                        color = BorderGray,
                        thickness = 1.dp,
                    )
                }
            }
        }
    }
}

@Composable
fun SubcategoryGrid(
    subcategories: List<CategoryUiItemModel>,
    onSubcategoryClick: (String) -> Unit,
) {
    val rows = subcategories.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        rows.forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                rowItems.forEach { item ->
                    SubcategoryCard(
                        subcategory = item,
                        modifier = Modifier.weight(1f),
                        onClick = { onSubcategoryClick(item.id) },
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun SubcategoryCard(subcategory: CategoryUiItemModel, modifier: Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderGray),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PrimaryCyan.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        subcategory.icon.icon,
                        contentDescription = null,
                        tint = PrimaryCyan,
                        modifier = Modifier.size(18.dp),
                    )
                }

                Text(
                    text = "${subcategory.usedPercentage}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black,
                )
            }

            Spacer(Modifier.height(10.dp))
            Text(
                text = subcategory.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subcategory.totalBudget.formatCurrency(),
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
            )
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { subcategory.usedPercentage / 100f },
                modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(50)),
                color = PrimaryCyan,
                trackColor = BorderGray,
            )
        }
    }
}

@Composable
fun BudgetSummaryCard(totalBudget: String, used: String, remaining: String, usedPersentage: Int) {
    Box(
        modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            PrimaryCyan,
                            SecondaryDeepPurple,
                        ),

                        ),
                )
                .padding(20.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Total Anggaran",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    letterSpacing = 0.5.sp,
                )
                Box(
                    modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(PrimaryCyan.copy(alpha = 0.5f))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "$usedPersentage% Terpakai",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = totalBudget,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row {
                    Text(
                        text = "Terpakai: ",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                    )
                    Text(
                        text = used,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                }

                Row {
                    Text(
                        text = "Sisa: ",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                    )
                    Text(
                        text = remaining,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                }


            }
            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { (usedPersentage / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f),
            )
        }
    }
}

// Remove the unused dummyTransactions list from here if it's already defined or redundant.
// It seems it was used for the preview. I will keep it but remove the duplicate if any.
// Actually, it's defined at the top of the file.

// Also clean up DetailCategoryTopBar if it's truly not used.
// DetailCategoryContent uses TopAppBar.

private val RangeProvider = CartesianLayerRangeProvider.auto()
private val CurrencyFormatter = CartesianValueFormatter { _, value, _ ->
    value.toLong().formatCompact()
}
private val MarkerValueFormatter = DefaultCartesianMarker.ValueFormatter { _, targets ->
    targets.joinToString { target ->
        // ganti `target.y` sesuai properti asli, aku belum yakin nama field-nya di versi kamu
        target.x.toLong().formatCompact()
    }
}

@Composable
fun WeeklyTrendChart(
    data: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
                .fillMaxWidth()
                .height(220.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, BorderGray),
    ) {
        if (data.isEmpty() || labels.isEmpty()) {
            WeeklyTrendEmptyState(modifier = Modifier.fillMaxSize())
        } else {
            WeeklyTrendChartContent(data = data, labels = labels)
        }
    }
}

@Composable
private fun WeeklyTrendEmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(16.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.ShowChart,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(32.dp),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Belum ada data transaksi minggu ini",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
            )
        }
    }
}

@Composable
private fun WeeklyTrendChartContent(
    data: List<Float>,
    labels: List<String>,
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            lineSeries {
                series(x = data.indices.toList(), y = data)
            }
        }
    }

    CartesianChartHost(
        modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(8.dp),
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(Fill(PrimaryCyan)),
                        areaFill = LineCartesianLayer.AreaFill.single(
                            Fill(
                                Brush.verticalGradient(
                                    listOf(PrimaryCyan.copy(alpha = 0.4f), Color.Transparent),
                                ),
                            ),
                        ),
                        interpolator = LineCartesianLayer.Interpolator.catmullRom(),
                    ),
                ),
                rangeProvider = RangeProvider,
            ),
            startAxis = VerticalAxis.rememberStart(
                label = rememberTextComponent(style = TextStyle(color = TextTertiary)),
                guideline = rememberAxisGuidelineComponent(fill = Fill(color = BorderGray)),
                valueFormatter = CurrencyFormatter,
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                label = rememberTextComponent(style = TextStyle(color = TextTertiary)),
                guideline = rememberAxisGuidelineComponent(fill = Fill(color = BorderGray)),
                valueFormatter = { _, value, _ -> labels.getOrElse(value.toInt()) { "" } },
            ),
            marker = rememberDefaultCartesianMarker(
                label = rememberTextComponent(style = TextStyle(color = PrimaryCyan)),
                valueFormatter = MarkerValueFormatter,
            ),
        ),
        modelProducer = modelProducer,
    )
}

@Composable
fun QuickActionsRow(
    onEditBudgetClick: () -> Unit,
    onSetNotificationClick: () -> Unit,
    onAddNoteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        QuickActionItem(
            icon = Icons.Outlined.Edit,
            label = "Edit Budget",
            onClick = onEditBudgetClick,
            modifier = Modifier.weight(1f),
        )
        QuickActionItem(
            icon = Icons.Outlined.Notifications,
            label = "Set Notification",
            onClick = onSetNotificationClick,
            modifier = Modifier.weight(1f),
        )
        QuickActionItem(
            icon = Icons.Outlined.Description,
            label = "Add Note",
            onClick = onAddNoteClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = PrimaryCyan,
                modifier = Modifier.size(22.dp),
            )
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F2937),
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailCategoryContentPreview() {
    NexCashApplicationTheme {
        DetailCategoryContent(
            state = DetailCategoryUiModel(
                category = CategoryUiItemModel(
                    id = "1",
                    name = "Transportasi",
                    icon = CategoryIcons.fromId("5"), // sesuaikan dgn tipe icon kamu
                    totalBudget = 1_500_000,
                    usedBudget = 1_050_000,
                    typeCategory = TypeCategory.EXPENSE,
                    color = PrimaryCyan,
                ),

                subCategories = listOf(
                    CategoryUiItemModel(
                        id = "sub1",
                        name = "Bensin",
                        icon = CategoryIcons.fromId("5"),
                        totalBudget = 450_000,
                        usedBudget = 270_000,
                        typeCategory = TypeCategory.EXPENSE,
                        color = PrimaryCyan,

                        ),
                    CategoryUiItemModel(
                        id = "sub2",
                        name = "Parkir",
                        icon = CategoryIcons.fromId("5"),
                        totalBudget = 120_000,
                        usedBudget = 48_000,
                        typeCategory = TypeCategory.EXPENSE,
                        color = PrimaryCyan,

                        ),
                    CategoryUiItemModel(
                        id = "sub3",
                        name = "Ojek Online",
                        icon = CategoryIcons.fromId("5"),
                        totalBudget = 380_000,
                        usedBudget = 323_000,
                        typeCategory = TypeCategory.EXPENSE,
                        color = PrimaryCyan,

                        ),
                    CategoryUiItemModel(
                        id = "sub4",
                        name = "Tol & Tiket",
                        icon = CategoryIcons.fromId("5"),
                        totalBudget = 100_000,
                        usedBudget = 25_000,
                        typeCategory = TypeCategory.EXPENSE,
                        color = PrimaryCyan,

                        ),
                ),
                recentTransactions = dummyTransactions,
            ),
            onEvent = {},
        )
    }
}