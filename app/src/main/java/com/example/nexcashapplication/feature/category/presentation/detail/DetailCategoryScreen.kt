package com.example.nexcashapplication.feature.category.presentation.detail;

import android.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.nexcashapplication.core.component.FormField
import com.example.nexcashapplication.core.component.TransactionItem
import com.example.nexcashapplication.core.domain.model.TransactionItemUi
import com.example.nexcashapplication.feature.category.presentation.detail.model.SubCategoryItemUi
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

val dummySubCategories = listOf(
    SubCategoryItemUi(
        id = 1L,
        name = "Makan di Luar",
        icon = Icons.Default.Settings,
        amount = "Rp450.000",
        percentage = 35,
    ),
    SubCategoryItemUi(
        id = 2L,
        name = "Groceries",
        icon = Icons.Default.ShoppingCart,
        amount = "Rp320.000",
        percentage = 25,
    ),
    SubCategoryItemUi(
        id = 3L,
        name = "Kopi & Snack",
        icon = Icons.Default.Notifications,
        amount = "Rp180.000",
        percentage = 14,
    ),
    SubCategoryItemUi(
        id = 4L,
        name = "Delivery/Ojol",
        icon = Icons.Default.Notifications,
        amount = "Rp150.000",
        percentage = 12,
    ),
)

val dummyTransactions = listOf(
    TransactionItemUi(
        id = "1",
        title = "Gaji Bulanan",
        subtitle = "BCA",
        time = "09:00",
        amount = 5_000_000,
        isIncome = true,
        category = "Gaji",
        iconRes = R.drawable.ic_media_ff,
    ),
    TransactionItemUi(
        id = "2",
        title = "Makan Siang",
        subtitle = "GoPay",
        time = "12:30",
        amount = 35_000,
        isIncome = false,
        category = "Makanan",
        iconRes = R.drawable.ic_lock_lock,
    ),
    TransactionItemUi(
        id = "3",
        title = "Beli Buku",
        subtitle = "OVO",
        time = "14:15",
        amount = 120_000,
        isIncome = false,
        category = "Pendidikan",
        iconRes = R.drawable.ic_dialog_map,
    ),
    TransactionItemUi(
        id = "4",
        title = "Bonus Freelance",
        subtitle = "Mandiri",
        time = "16:45",
        amount = 750_000,
        isIncome = true,
        category = "Freelance",
        iconRes = R.drawable.ic_dialog_map,
    ),
    TransactionItemUi(
        id = "5",
        title = "Ngopi di Kedai",
        subtitle = "Dana",
        time = "17:20",
        amount = 25_000,
        isIncome = false,
        category = "Kopi & Snack",
        iconRes = R.drawable.ic_lock_silent_mode_off,
    ),
    TransactionItemUi(
        id = "6",
        title = "Bayar Listrik",
        subtitle = "BCA",
        time = "19:00",
        amount = 250_000,
        isIncome = false,
        category = "Tagihan",
        iconRes = R.drawable.ic_input_get,
    ),
    TransactionItemUi(
        id = "7",
        title = "Beli Baju",
        subtitle = "ShopeePay",
        time = "20:10",
        amount = 180_000,
        isIncome = false,
        category = "Belanja",
        iconRes = R.drawable.ic_input_delete,
    ),
    TransactionItemUi(
        id = "8",
        title = "Transfer dari Teman",
        subtitle = "BRI",
        time = "21:30",
        amount = 100_000,
        isIncome = true,
        category = "Transfer",
        iconRes = R.drawable.ic_input_get,
    ),
)

@Composable
fun DetailCategoryScreen(
    viewModel: DetailCategoryViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {

    DetailCategoryContent()

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailCategoryContent(
    categoryName: String = "Transportasi",
    categoryIcon: ImageVector = Icons.Default.Email,
    totalBudget: String = "Rp 1.500.000",
    used: String = "Rp 1.050.000",
    remaining: String = "Rp 450.000",
    usedPercentage: Int = 70,
    subcategories: List<SubCategoryItemUi> = dummySubCategories,
    transactions: List<TransactionItemUi> = dummyTransactions,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = null,
                            tint = PrimaryCyan,
                            modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(20.dp),
                        )
                        Text(
                            text = categoryName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                },
            )
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
                totalBudget = totalBudget,
                used = used,
                remaining = remaining,
                usedPersentage = usedPercentage,
            )

            FormField(
                title = "Tren Pengeluaran",
                actionText = "Lihat Semua",
                onActionClick = { },
            )

            WeeklyTrendChart()

            FormField(
                title = "SubCategory",
                actionText = "Lihat Semua",
                onActionClick = { },
            )

            SubcategoryGrid(
                subcategories = subcategories,
                onSubcategoryClick = { },
            )

            FormField(
                title = "Transaksi Terakhir",
                icon = Icons.Default.ShoppingCart,
                actionText = "Lihat Semua",
            )

            TransactionListCard(
                transactions = transactions,
                onLoadMoreClick = { },
            )

            Spacer(Modifier.height(16.dp)) // pengganti contentPadding bottom
        }
    }
}


@Composable
fun TransactionListCard(
    transactions: List<TransactionItemUi>,
    onLoadMoreClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, PrimaryCyan),
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            transactions.forEachIndexed { index, transactionUi ->
                TransactionItem(transactionUi) {}
            }
        }
    }
}

@Composable
fun SubcategoryGrid(
    subcategories: List<SubCategoryItemUi>,
    onSubcategoryClick: (Long) -> Unit,
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
fun SubcategoryCard(subcategory: SubCategoryItemUi, modifier: Modifier, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderGray),
        modifier = modifier.clickable(onClick = onClick),
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
                        subcategory.icon,
                        contentDescription = null,
                        tint = PrimaryCyan,
                        modifier = Modifier.size(18.dp),
                    )
                }

                Text(
                    text = "${subcategory.percentage}%",
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
                text = subcategory.amount,
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
            )
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { subcategory.percentage / 100f },
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
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    letterSpacing = 0.5.sp,
                )
                Box(
                    modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(PrimaryCyan)
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
                        color = Color.White,
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
                        color = Color(0xFF6B7280),
                    )
                    Text(
                        text = remaining,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6366F1),
                    )
                }


            }
            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { usedPersentage / 100f },
                modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50)),
                color = Color(0xFF6366F1),
                trackColor = Color(0xFFC7CCF5),
            )
        }
    }
}

@Composable
fun CategoryDetailTopBar(
    categoryName: String,
    categoryIcon: ImageVector,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
) {
    Row(
        modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = {

            },
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
        }

        Icon(
            imageVector = categoryIcon,
            contentDescription = null,
            tint = PrimaryCyan,
            modifier = Modifier
                    .padding(start = 4.dp, end = 8.dp)
                    .size(20.dp),
        )

        Text(
            text = categoryName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )

        IconButton(
            onClick = {

            },
        ) {
            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
        }
    }

}

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
        border = BorderStroke(1.dp, PrimaryCyan),
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

@Preview
@Composable
private fun DetailCategoryScreenPreview() {
    NexCashApplicationTheme {
        DetailCategoryScreen()
    }
}