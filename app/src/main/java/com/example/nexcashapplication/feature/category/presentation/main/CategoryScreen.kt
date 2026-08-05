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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.category.presentation.addnew.constant.CategoryIcons
import com.example.nexcashapplication.feature.category.presentation.main.model.CategoryItem
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import kotlinx.coroutines.flow.distinctUntilChanged

// ============ DUMMY DATA ============
// (Removed as it is now provided by state)

@Composable
fun CategoryScreen(
    modifier: Modifier = Modifier,
    viewModel: CategoryViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CategoryEffect.NavigateToAddCategory -> navController.navigate(
                    Route.CategoryRoute.AddNew(effect.categoryId, effect.parentId),
                )

                is CategoryEffect.NavigateToDetail -> navController.navigate(
                    Route.CategoryRoute.Detail(effect.categoryId),
                )

                else -> {}
            }
        }
    }

    val tabList = remember { TypeCategory.entries }

    val pagerState = rememberPagerState(
        initialPage = tabList.indexOf(state.selectedTabCategory).coerceAtLeast(0),
        pageCount = { tabList.size },
    )


    // Pager -> State (kalau user swipe manual)
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
                .distinctUntilChanged()
                .collect { page ->
                    if (page in tabList.indices) {
                        viewModel.onEvent(CategoryEvent.OnTabCategoryClick(tabList[page]))
                    }
                }
    }

    // State -> Pager (kalau klik tab)
    LaunchedEffect(state.selectedTabCategory) {
        val targetPage = tabList.indexOf(state.selectedTabCategory)
        if (targetPage != -1 && pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    CategoryContent(
        modifier = modifier,
        pagerState = pagerState,
        state = state,
        onEvent = viewModel::onEvent,
    )
}

// ============ CONTENT SCREEN ============
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryContent(
    pagerState: PagerState,
    state: CategoryState,
    modifier: Modifier = Modifier,
    onEvent: (CategoryEvent) -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kategori",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            onEvent(CategoryEvent.OnAddKategoriClick)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah",
                            tint = Color(0xFF29B6F6),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(CategoryEvent.OnAddKategoriClick)
                },
                containerColor = Color(0xFF29B6F6),
                shape = CircleShape,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Kategori", tint = Color.White)
            }
        },
        containerColor = Color.White,
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {

            Spacer(Modifier.height(4.dp))

            // Search bar
            CategorySearchBar(
                query = state.searchQuery,
                onQueryChange = { onEvent(CategoryEvent.OnSearchQueryChange(it)) },
            )

            // Budget summary card

            BudgetSummaryCard(
                totalBudget = state.totalAnggaran,
                remainingBudget = state.sisaAnggaran,
                selectedTab = state.selectedTabCategory,
                totalPemasukan = state.totalPemasukan,
            )

            CategoryPager(
                incomeList = state.incomeList,
                expanseList = state.expenseList,
                categorySearchList = state.categorySearchList,
                searchQuery = state.searchQuery,
                pagerState = pagerState,
                onTabCategoryClick = { type ->
                    onEvent(CategoryEvent.OnTabCategoryClick(type))
                },
                onCategoryClick = { id ->
                    onEvent(CategoryEvent.OnKategoriClick(id))
                },
            )


            // Add new category
            AddCategoryButton(
                onClick = {
                    onEvent(CategoryEvent.OnAddKategoriClick)
                },
            )

            Spacer(Modifier.height(80.dp))
        }
    }
}

// ============ SEARCH BAR ============
@Composable
fun CategorySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
        singleLine = true,
        placeholder = {
            Text("Cari kategori...")
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
            )
        },
//        trailingIcon = {
//            Icon(
//                imageVector = Icons.Default.Lock,
//                contentDescription = "Filter",
//            )
//        },
        shape = RoundedCornerShape(50.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF2F2F2),
            unfocusedContainerColor = Color(0xFFF2F2F2),
            disabledContainerColor = Color(0xFFF2F2F2),

            focusedBorderColor = PrimaryCyan,
            unfocusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,

            cursorColor = Color.Black,
        ),
    )
}

// ============ BUDGET SUMMARY CARD ============
@Composable
fun BudgetSummaryCard(
    selectedTab: TypeCategory,
    totalBudget: Long,
    remainingBudget: Long,
    totalPemasukan: Long,  // dari incomeList
) {
    Row(
        modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE8F7FD))
                .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF29B6F6).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountBalanceWallet,
                contentDescription = null,
                tint = Color(0xFF29B6F6),
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(Modifier.width(12.dp))

        if (selectedTab == TypeCategory.EXPENSE) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Total Anggaran", fontSize = 12.sp, color = Color(0xFF7A8A99))
                Text(
                    text = "Rp ${(totalBudget).formatCurrency()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Sisa", fontSize = 12.sp, color = Color(0xFF7A8A99))
                Text(
                    text = (remainingBudget).formatCurrency(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
            }
        } else {
            Column(modifier = Modifier.weight(1f)) {
                Text("Total Pemasukan Bulan Ini", fontSize = 12.sp, color = Color(0xFF7A8A99))
                Text(
                    text = "Rp ${(totalPemasukan).formatCurrency()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPager(
    expanseList: List<CategoryItem>,
    incomeList: List<CategoryItem>,
    categorySearchList: List<CategoryItem>,
    searchQuery: String,
    pagerState: PagerState,
    onTabCategoryClick: (TypeCategory) -> Unit,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = TypeCategory.entries
    Column(modifier = modifier) {
        PrimaryTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.White,
            contentColor = PrimaryCyan,
            indicator = {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(
                        selectedTabIndex = pagerState.currentPage,
                        matchContentSize = false,
                    ),
                    width = Dp.Unspecified,
                    color = PrimaryCyan,
                )
            },

            ) {
            tabs.forEach { type ->
                Tab(
                    selected = tabs[pagerState.currentPage] == type,
                    onClick = { onTabCategoryClick(type) },
                    text = {
                        Text(
                            text = type.label,
                            fontSize = 14.sp,
                            fontWeight = if (tabs[pagerState.currentPage] == type) FontWeight.Bold else FontWeight.Medium,
                        )
                    },
                    selectedContentColor = PrimaryCyan,
                    unselectedContentColor = Color(0xFF7A8A99),
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) { pageIndex ->
            val normalList = when (pageIndex) {
                0 -> expanseList
                1 -> incomeList
                else -> emptyList()
            }

            val displayList = if (searchQuery.isNotBlank()) {
                categorySearchList  // lagi search -> pakai hasil search
            } else {
                normalList  // nggak search -> pakai data asli
            }

            CategoryGrid(
                list = displayList,
                onItemClick = { item -> onCategoryClick(item.id) },
            )
        }
    }
}

// ============ CATEGORY GRID (manual 2 columns, non-lazy, nested inside LazyColumn) ============
@Composable
fun CategoryGrid(
    list: List<CategoryItem>,
    onItemClick: (CategoryItem) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (list.isEmpty()) {
            Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Tidak ada kategori",
                    color = Color(0xFF9E9E9E),
                    fontSize = 14.sp,
                )
            }
        }

        list.chunked(2).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                rowItems.forEach { item ->
                    CategoryCard(
                        item = item,
                        modifier = Modifier.weight(1f),
                        onClick = { onItemClick(item) },
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
    onClick: () -> Unit,
) {
    val isOverBudget = item.persentase > 100
    val progressColor = if (isOverBudget) PrimaryCyan else Color(0xFF29B6F6)
    val badgeBg = if (isOverBudget) Color(0xFFFFEBEE) else Color(0xFFF2F2F2)
    val badgeText = if (isOverBudget) Color(0xFFE53935) else Color(0xFF616161)

    Column(
        modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFEFEFEF), RoundedCornerShape(16.dp))
                .clickable { onClick() }
                .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(item.iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = item.icon.icon,
                    contentDescription = item.nama,
                    tint = item.iconColor,
                    modifier = Modifier.size(20.dp),
                )
            }
            Box(
                modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(badgeBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(
                    text = "${item.persentase}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = badgeText,
                )
            }
        }

        Spacer(Modifier.height(10.dp))
        Text(item.nama, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        Text(item.tipe.label, fontSize = 12.sp, color = Color(0xFF9E9E9E))

        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = (item.terpakai).formatCurrency(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
            Text(
                text = " / ${(item.budget).formatCurrency()}",
                fontSize = 11.sp,
                color = Color(0xFF9E9E9E),
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
            trackColor = Color(0xFFE8F7FD),
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
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f),
                        ),
                        cornerRadius = CornerRadius(16.dp.toPx()),
                    )
                }
                .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color(0xFF757575),
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "Tambah Kategori Baru",
                color = Color(0xFF757575),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}


// ============ PREVIEW ============

private val dummyExpenseList = listOf(
    CategoryItem(
        id = "1L",
        nama = "Makan & Minum",
        tipe = TypeCategory.EXPENSE,
        icon = CategoryIcons.makanan[0], // "restaurant"
        iconColor = Color(0xFF29B6F6),
        terpakai = 850_000L,
        budget = 1_000_000L,
    ),
    CategoryItem(
        id = "2L",
        nama = "Transportasi",
        tipe = TypeCategory.EXPENSE,
        icon = CategoryIcons.transportasi[1], // "car"
        iconColor = Color(0xFF66BB6A),
        terpakai = 1_200_000L,
        budget = 1_000_000L, // over budget, buat test badge merah
    ),
    CategoryItem(
        id = "3L",
        nama = "Hiburan",
        tipe = TypeCategory.EXPENSE,
        icon = CategoryIcons.hiburan[0], // "movie"
        iconColor = Color(0xFFFFA726),
        terpakai = 200_000L,
        budget = 500_000L,
    ),
)

private val dummyIncomeList = listOf(
    CategoryItem(
        id = "4L",
        nama = "Gaji Bulanan",
        tipe = TypeCategory.INCOME,
        icon = CategoryIcons.lainnya[0], // "location" - ganti kalau ada icon income khusus
        iconColor = Color(0xFF29B6F6),
        terpakai = 4_500_000L,
        budget = 5_000_000L,
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun PreviewCategoryScreen() {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { TypeCategory.entries.size },
    )

    NexCashApplicationTheme {
        CategoryContent(
            pagerState = pagerState,
            state = CategoryState(
                isLoading = false,
                expenseList = dummyExpenseList,
                incomeList = dummyIncomeList,
                searchQuery = "",
                selectedTabCategory = TypeCategory.EXPENSE,
            ),
            onEvent = {}, // preview nggak butuh handle event beneran
        )
    }
}

@Preview(showBackground = true, name = "Empty State")
@Composable
private fun PreviewCategoryScreenEmpty() {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { TypeCategory.entries.size },
    )

    NexCashApplicationTheme {
        CategoryContent(
            pagerState = pagerState,
            state = CategoryState(
                isLoading = false,
                expenseList = emptyList(),
                incomeList = emptyList(),
            ),
            onEvent = {},
        )
    }
}
