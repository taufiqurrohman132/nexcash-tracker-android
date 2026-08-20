package com.example.nexcashapplication.feature.category.presentation.detail;

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.core.navigations.Route.CategoryRoute.AddNew
import com.example.nexcashapplication.feature.category.presentation.addnew.constant.CategoryIcons
import com.example.nexcashapplication.feature.category.presentation.detail.model.CategoryDetail
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import com.example.nexcashapplication.ui.theme.SecondaryDeepPurple
import com.example.nexcashapplication.ui.theme.TextTertiary
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart

val dummyTransactions = listOf(
    TransactionItemUiModel(
        id = "1",
        name = "Gaji Bulanan",
        time = "08:00",
        amount = 8_500_000,
        isIncome = true,
        category = "Gaji",
        account = "BCA",
        icon = Icons.Default.AccountBalanceWallet,
    ),
    TransactionItemUiModel(
        id = "2",
        name = "Makan Siang",
        time = "12:30",
        amount = 35_000,
        isIncome = false,
        category = "Makanan & Minuman",
        account = "Cash",
        icon = Icons.Default.Restaurant,
    ),
    TransactionItemUiModel(
        id = "3",
        name = "Langganan Netflix",
        time = "10:15",
        amount = 54_000,
        isIncome = false,
        category = "Hiburan",
        account = "Gopay",
        icon = Icons.Default.Movie,
    ),
    TransactionItemUiModel(
        id = "4",
        name = "Bensin Motor",
        time = "07:45",
        amount = 25_000,
        isIncome = false,
        category = "Transportasi",
        account = "Cash",
        icon = Icons.Default.LocalGasStation,
    ),
    TransactionItemUiModel(
        id = "5",
        name = "Freelance Project",
        time = "16:20",
        amount = 1_200_000,
        isIncome = true,
        category = "Freelance",
        account = "BCA",
        icon = Icons.Default.Work,
    ),
    TransactionItemUiModel(
        id = "6",
        name = "Belanja Bulanan",
        time = "19:00",
        amount = 450_000,
        isIncome = false,
        category = "Belanja",
        account = "Gopay",
        icon = Icons.Default.ShoppingCart,
    ),
    TransactionItemUiModel(
        id = "7",
        name = "Tagihan Listrik",
        time = "09:10",
        amount = 180_000,
        isIncome = false,
        category = "Tagihan",
        account = "BCA",
        icon = Icons.Default.Bolt,
    ),
    TransactionItemUiModel(
        id = "8",
        name = "Transfer dari Teman",
        time = "14:05",
        amount = 200_000,
        isIncome = true,
        category = "Tanpa Kategori",
        account = "Akun Dihapus",
        icon = Icons.Default.Category,
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
                    AddNew(categoryId = "", parentId = effect.parentId),
                )

                is DetailCategoryEffect.NavigateToAddTransaction -> {
                    navController.navigate(Route.TransactionRoute.AddNew)
                }
                is DetailCategoryEffect.NavigateToAllSubCategories -> {
                    // Navigate to all subcategories if you have a screen for it
                }
                is DetailCategoryEffect.NavigateToEditCategory -> navController.navigate(
                    AddNew(effect.categoryId),
                )

                is DetailCategoryEffect.NavigateToSubCategoryDetail -> navController.navigate(
                    Route.CategoryRoute.Detail(effect.subCategoryId)
                )
                is DetailCategoryEffect.NavigateToSubCategoryManagement -> {
                    // Navigate to management
                }
                is DetailCategoryEffect.NavigateToTransactionDetail -> navController.navigate(
                    Route.TransactionRoute.Detail(effect.transactionId)
                )
                DetailCategoryEffect.ShowMenuBottomSheet -> {
                    // Show sheet
                }
                is DetailCategoryEffect.ShowSnackbar -> {
                    // Show snackbar
                }
                is DetailCategoryEffect.NavigateToDeleteCategory -> navController.navigate(
                    Route.CategoryRoute.Delete(
                        categoryId = effect.categoryId,
                        typeCategory = effect.typeCategory,
                    ),
                )
            }
        }
    }

    DetailCategoryContent(
        state = state,
        onEvent = viewModel::onEvent,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailCategoryContent(
    state: DetailCategoryState,
    onEvent: (DetailCategoryEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        state.category?.icon?.icon?.let {
                            Icon(
                                imageVector = it,
                                contentDescription = null,
                                tint = PrimaryCyan,
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
                    Row {
                        IconButton(onClick = { onEvent(DetailCategoryEvent.OnEditClick) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { onEvent(DetailCategoryEvent.OnDeleteClick) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(DetailCategoryEvent.OnAddSubCategoryClick) },
                containerColor = PrimaryCyan,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    tint = Color.White,
                    contentDescription = "Add Sub Category",
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

            FormField(
                title = "Tren Pengeluaran",
                actionText = "7 Hari Terakhir",
                onActionClick = { },
            )

            WeeklyTrendChart()

            FormField(
                title = "Sub Kategori",
                actionText = "Lihat Semua",
                onActionClick = { onEvent(DetailCategoryEvent.OnSeeAllSubCategoryClick) },
            )

            SubcategoryGrid(
                subcategories = state.subCategories,
                onSubcategoryClick = { id -> onEvent(DetailCategoryEvent.OnSubCategoryClick(id)) },
            )

            FormField(
                title = "Transaksi Terakhir",
                icon = Icons.Default.Replay,
                actionText = "Lihat Semua",
                onActionClick = { onEvent(DetailCategoryEvent.OnLoadMoreTransactionsClick) }
            )

            TransactionListCard(
                transactions = state.recentTransactions,
                onItemClick = { id -> onEvent(DetailCategoryEvent.OnTransactionClick(id)) }
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
                    onClick = { onItemClick(transactionUi.id ?: "") }
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
    subcategories: List<CategoryDetail>,
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
fun SubcategoryCard(subcategory: CategoryDetail, modifier: Modifier, onClick: () -> Unit) {
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

@Composable
fun WeeklyTrendChart(
    modifier: Modifier = Modifier,
    data: List<Float> = listOf(300f, 350f, 280f, 450f, 550f, 850f, 650f),
    labels: List<String> = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"),
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            lineSeries { series(data) }
        }
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(1.dp, BorderGray),
    ) {
        CartesianChartHost(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(), startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = { _, value, _ ->
                        labels.getOrElse(value.toInt()) { "" }
                    },
                ),
            ),
            modelProducer = modelProducer,
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun DetailCategoryContentPreview() {
    NexCashApplicationTheme {
        DetailCategoryContent(
            state = DetailCategoryState(
                isLoading = false,
                category = CategoryDetail(
                    id = "1",
                    name = "Transportasi",
                    icon = CategoryIcons.fromId("5"), // sesuaikan dgn tipe icon kamu
                    totalBudget = 1_500_000,
                    usedBudget = 1_050_000,
                    typeCategory = TypeCategory.EXPENSE,

                    ),

                subCategories = listOf(
                    CategoryDetail(
                        id = "sub1",
                        name = "Bensin",
                        icon = CategoryIcons.fromId("5"),
                        totalBudget = 450_000,
                        usedBudget = 270_000,
                        typeCategory = TypeCategory.EXPENSE,

                        ),
                    CategoryDetail(
                        id = "sub2",
                        name = "Parkir",
                        icon = CategoryIcons.fromId("5"),
                        totalBudget = 120_000,
                        usedBudget = 48_000,
                        typeCategory = TypeCategory.EXPENSE,

                        ),
                    CategoryDetail(
                        id = "sub3",
                        name = "Ojek Online",
                        icon = CategoryIcons.fromId("5"),
                        totalBudget = 380_000,
                        usedBudget = 323_000,
                        typeCategory = TypeCategory.EXPENSE,

                        ),
                    CategoryDetail(
                        id = "sub4",
                        name = "Tol & Tiket",
                        icon = CategoryIcons.fromId("5"),
                        totalBudget = 100_000,
                        usedBudget = 25_000,
                        typeCategory = TypeCategory.EXPENSE,

                        ),
                ),
                recentTransactions = dummyTransactions,
            ),
            onEvent = {},
        )
    }
}