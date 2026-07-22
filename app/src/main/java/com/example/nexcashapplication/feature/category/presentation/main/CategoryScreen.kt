package com.example.nexcashapplication.feature.category.presentation.main


// ===== Compose Foundation =====

// ===== Material Icons =====

// ===== Material3 =====

// ===== Compose Runtime & UI =====
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.feature.category.presentation.main.model.CategoryFilter
import com.example.nexcashapplication.feature.category.presentation.main.model.CategoryItem
import com.example.nexcashapplication.feature.category.presentation.navigation.CategoryNavigationEvent
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme

// ============ DUMMY DATA ============
val dummyKategori = listOf(
    CategoryItem(
        1,
        "Makanan",
        CategoryFilter.EXPENSE,
        Icons.Default.ShoppingCart,
        Color(0xFFFFE0CC),
        2_150_000,
        3_000_000
    ),
    CategoryItem(
        2,
        "Transportasi",
        CategoryFilter.INCOME,
        Icons.Default.FavoriteBorder,
        Color(0xFFD9E4FF),
        850_000,
        1_000_000
    ),
    CategoryItem(
        3,
        "Belanja",
        CategoryFilter.EXPENSE,
        Icons.Default.Done,
        Color(0xFFFFD9E8),
        1_450_000,
        1_200_000
    ),
    CategoryItem(
        4,
        "Kesehatan",
        CategoryFilter.INCOME,
        Icons.Default.Done,
        Color(0xFFD4F5E0),
        300_000,
        500_000
    ),
    CategoryItem(
        5,
        "Tagihan",
        CategoryFilter.EXPENSE,
        Icons.Default.Email,
        Color(0xFFFFF3D0),
        1_200_000,
        1_500_000
    ),
    CategoryItem(
        6,
        "Hiburan",
        CategoryFilter.INCOME,
        Icons.Default.Notifications,
        Color(0xFFEBDCFF),
        450_000,
        800_000
    ),
    CategoryItem(
        7,
        "Investasi",
        CategoryFilter.EXPENSE,
        Icons.Default.Search,
        Color(0xFFD4EAFF),
        2_000_000,
        2_000_000
    ),
    CategoryItem(
        8,
        "Lainnya",
        CategoryFilter.INCOME,
        Icons.Default.LocationOn,
        Color(0xFFEDEDED),
        120_000,
        300_000
    ),
)

@Composable
fun CategoryScreen(
    modifier: Modifier = Modifier,
    viewModel: CategoryViewModel = hiltViewModel(),
    onNavigateAddCategory: () -> Unit = {},
    onNavigateCategoryDetail: (Long) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is CategoryNavigationEvent.NavigateToAddCategory -> onNavigateAddCategory()
                is CategoryNavigationEvent.NavigateToDetail -> onNavigateCategoryDetail(event.categoryId)
            }
        }
    }

    CategoryContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

// ============ CONTENT SCREEN ============
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryContent(
    state: CategoryState,
    categoryList: List<CategoryItem> = dummyKategori,
    totalBudget: Long = 10_500_000,
    remainingBudget: Long = 1_930_000,
    onEvent: (CategoryEvent) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(CategoryFilter.EXPENSE) }

    val filteredList = categoryList.filter {
        it.nama.contains(searchQuery, ignoreCase = true) &&
                it.tipe.label == if (selectedFilter == CategoryFilter.EXPENSE) "Pengeluaran" else "Pemasukan"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kategori",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = {
                        onEvent(CategoryEvent.OnAddKategoriClick)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah",
                            tint = Color(0xFF29B6F6)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(CategoryEvent.OnAddKategoriClick)
                },
                containerColor = Color(0xFF29B6F6),
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Kategori", tint = Color.White)
            }
        },
        containerColor = Color.White
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search bar
            item {
                Spacer(Modifier.height(4.dp))
                CategorySearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
            }

            // Budget summary card
            item {
                BudgetSummaryCard(
                    totalBudget = totalBudget,
                    remainingBudget = remainingBudget
                )
            }

            // Filter chips
            item {
                FilterChipsRow(
                    selected = selectedFilter,
                    onSelect = { selectedFilter = it }
                )
            }

            // Category grid (2 columns) - wrapped manually since LazyVerticalGrid
            // can't be nested directly inside LazyColumn
            item {
                CategoryGrid(
                    list = filteredList,
                    onItemClick = {
                        onEvent(CategoryEvent.OnKategoriClick(it.id))
                    }
                )
            }

            // Add new category (dashed button)
            item {
                AddCategoryButton(onClick = {
                    onEvent(CategoryEvent.OnAddKategoriClick)
                })
                Spacer(Modifier.height(80.dp)) // spacing so it's not hidden behind FAB
            }
        }
    }
}

// ============ SEARCH BAR ============
@Composable
fun CategorySearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF2F2F2))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = Color(0xFF9E9E9E),
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    text = "Cari kategori pengeluaran...",
                    color = Color(0xFF9E9E9E),
                    fontSize = 14.sp
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = Color.Black),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Filter",
            tint = Color(0xFF9E9E9E),
            modifier = Modifier.size(20.dp)
        )
    }
}

// ============ BUDGET SUMMARY CARD ============
@Composable
fun BudgetSummaryCard(
    totalBudget: Long,
    remainingBudget: Long
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE8F7FD))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFF29B6F6).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountBox,
                contentDescription = null,
                tint = Color(0xFF29B6F6),
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Total Anggaran", fontSize = 12.sp, color = Color(0xFF7A8A99))
            Text(
                text = (totalBudget).formatCurrency(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("Sisa", fontSize = 12.sp, color = Color(0xFF7A8A99))
            Text(
                text = (remainingBudget).formatCurrency(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

// ============ FILTER CHIPS ============
@Composable
fun FilterChipsRow(
    selected: CategoryFilter,
    onSelect: (CategoryFilter) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FilterChipItem(
            text = "Pengeluaran",
            isSelected = selected == CategoryFilter.EXPENSE,
            onClick = { onSelect(CategoryFilter.EXPENSE) }
        )
        FilterChipItem(
            text = "Pemasukan",
            isSelected = selected == CategoryFilter.INCOME,
            onClick = { onSelect(CategoryFilter.INCOME) }
        )
    }
}

@Composable
fun FilterChipItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (isSelected) Color(0xFF29B6F6) else Color.White)
            .then(
                if (!isSelected) Modifier.border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(50))
                else Modifier
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color(0xFF616161),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ============ CATEGORY GRID (manual 2 columns, non-lazy, nested inside LazyColumn) ============
@Composable
fun CategoryGrid(
    list: List<CategoryItem>,
    onItemClick: (CategoryItem) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        list.chunked(2).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { item ->
                    CategoryCard(
                        item = item,
                        modifier = Modifier.weight(1f),
                        onClick = { onItemClick(item) }
                    )
                }
                // keep layout aligned when the row has an odd number of items
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// ============ CATEGORY CARD ============
@Composable
fun CategoryCard(
    item: CategoryItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isOverBudget = item.persentase > 100
    val progressColor = if (isOverBudget) Color(0xFFEF5350) else Color(0xFF29B6F6)
    val badgeBg = if (isOverBudget) Color(0xFFFFEBEE) else Color(0xFFF2F2F2)
    val badgeText = if (isOverBudget) Color(0xFFE53935) else Color(0xFF616161)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFEFEFEF), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(item.iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.nama,
                    tint = Color(0xFF444444),
                    modifier = Modifier.size(20.dp)
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(badgeBg)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "${item.persentase}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = badgeText
                )
            }
        }

        Spacer(Modifier.height(10.dp))
        Text(item.nama, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        Text(item.tipe.toString(), fontSize = 12.sp, color = Color(0xFF9E9E9E))

        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = (item.terpakai).formatCurrency(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = " / ${(item.budget).formatCurrency()}",
                fontSize = 11.sp,
                color = Color(0xFF9E9E9E)
            )
        }

        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { (item.persentase / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50)),
            color = progressColor,
            trackColor = Color(0xFFE8F7FD)
        )
    }
}

// ============ ADD CATEGORY BUTTON (dashed) ============
@Composable
fun AddCategoryButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .drawBehind {
                drawRoundRect(
                    color = Color(0xFFBDBDBD),
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                    ),
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            }
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color(0xFF757575),
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "Tambah Kategori Baru",
                color = Color(0xFF757575),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Preview
@Composable
private fun PreviewCategoryScreen() {
    NexCashApplicationTheme(
    ) {
        CategoryScreen()
    }

}