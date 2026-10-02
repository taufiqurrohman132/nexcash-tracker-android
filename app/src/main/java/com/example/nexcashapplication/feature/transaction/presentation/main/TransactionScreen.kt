package com.example.nexcashapplication.feature.transaction.presentation.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.component.IconCircular
import com.example.nexcashapplication.core.component.IconRounded
import com.example.nexcashapplication.core.component.MonthNavigator
import com.example.nexcashapplication.core.component.MonthPicker
import com.example.nexcashapplication.core.component.TransactionItem
import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel
import com.example.nexcashapplication.core.extension.formatCompact
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.core.navigations.Route
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType
import com.example.nexcashapplication.feature.transaction.presentation.main.component.TransactionEmptyScreen
import com.example.nexcashapplication.feature.transaction.presentation.main.model.CategoryNameItemUiModel
import com.example.nexcashapplication.feature.transaction.presentation.main.model.TransactionHeaderUiModel
import com.example.nexcashapplication.feature.transaction.presentation.main.model.TransactionUiGroupModel
import com.example.nexcashapplication.ui.theme.AccentTealGreen
import com.example.nexcashapplication.ui.theme.BackgroundLight
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import com.example.nexcashapplication.ui.theme.SuccessGreen
import com.example.nexcashapplication.ui.theme.TextTertiary
import com.example.nexcashapplication.ui.theme.WarningAmber
import java.time.YearMonth

@Composable
fun TransactionScreen(
    modifier: Modifier = Modifier,
    viewModel: TransactionViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { event ->
            when (event) {
                is TransactionEffect.NavigateToAddTransaction -> navController.navigate(
                    Route.TransactionRoute.AddEdit(),
                )

                is TransactionEffect.NavigateToDetail -> navController.navigate(
                    Route.TransactionRoute.Detail(event.transactionId),
                )

                is TransactionEffect.ShowSnackbar -> {

                }
            }
        }
    }

    TransactionContent(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionContent(
    state: TransactionState,
    onEvent: (TransactionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {

                    Text(
                        text = "Transaction",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                },
                actions = {
                    MonthNavigator(
                        month = state.selectedMonth,
                        onMonthSelected = { onEvent(TransactionEvent.OnMonthSelected(it)) },
                        onOpenPicker = { onEvent(TransactionEvent.OnOpenMonthPicker) },
                    )
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(TransactionEvent.OnAddTransactionClick) },
                containerColor = PrimaryCyan,
                shape = CircleShape,
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { onEvent(TransactionEvent.OnSearchQueryChange(it)) },
                placeholder = { Text("Cari Transaksi...") },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            onClick = {
                                onEvent(TransactionEvent.OnSearchClear)
                            },
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(50.dp),
                shape = RoundedCornerShape(50.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = PrimaryCyan,
                    unfocusedIndicatorColor = TextTertiary,
                    unfocusedContainerColor = BackgroundLight,
                    focusedContainerColor = BackgroundLight,
                ),
            )

            val allLabel = stringResource(R.string.semua) // ← ambil di sini dulu
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = buildList {
                        add(
                            CategoryNameItemUiModel(
                                id = null,
                                nameCategory = allLabel,
                            ),
                        )
                        addAll(state.categoryList)
                    },
                    key = { it?.id ?: null.hashCode() },
                ) { category ->
                    FilterChip(
                        selected = state.selectedCategoryId == category?.id,
                        onClick = { onEvent(TransactionEvent.OnCategorySelected(categoryId = category?.id)) },
                        label = { Text(category?.nameCategory ?: "") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedLabelColor = Color.White,
                            selectedContainerColor = PrimaryCyan,
                        ),
                        shape = RoundedCornerShape(50.dp),
                    )
                }
            }

            LazyColumn(
                modifier = modifier
                        .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                if (state.searchQuery.isNotBlank()) {
                    items(
                        state.trasactionsSearchList,
                        key = { it.id ?: it.hashCode() },
                    ) { item ->
                        Column {
                            TransactionItem(
                                transaction = item,
                                onClick = { onEvent(TransactionEvent.OnTransactionClick(item.id)) },
                            )
                            HorizontalDivider(color = BorderGray)
                        }
                    }
                } else {
                    // sumary card
                    item(key = "summary_card") {
                        Row(
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            SummaryCard(
                                title = "Hari Ini",
                                amount = state.todayTotal,
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.KeyboardArrowDown,
                                iconColor = Color.Red,
                            )
                            SummaryCard(
                                title = "Minggu Ini",
                                amount = state.weekTotal,
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.KeyboardArrowUp,
                                iconColor = WarningAmber,
                            )
                            SummaryCard(
                                title = "Sisa Budget",
                                amount = state.remainingBudget,
                                modifier = Modifier.weight(1f),
                                iconColor = SuccessGreen,
                                icon = Icons.Default.QrCodeScanner,

                                )
                        }
                    }

                    if (state.groupedTransactions.isEmpty()) {
                        item(key = "loading_or_empty") {
                            if (state.isLoading) {
                                // data belum sampai: tampil progres, jangan
                                // buru-buru bilang "tidak ada transaksi"
                                Box(
                                    modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(48.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else {
                                TransactionEmptyScreen(
                                    onAddClick = {
                                        onEvent(TransactionEvent.OnAddTransactionClick)
                                    },
                                )
                            }
                        }
                    } else {
                        state.groupedTransactions.forEach { uiGroupModel ->
                            val isExpanded =
                                !state.collapsGroups.contains(uiGroupModel.header.title)

                            stickyHeader(key = uiGroupModel.header.id) {
                                TransactionGroupHeader(
                                    label = uiGroupModel.header.title,
                                    count = uiGroupModel.listTransaction.size,
                                    total = uiGroupModel.header.totalAmount,
                                    isExpanded = isExpanded,
                                    onToggle = { onEvent(TransactionEvent.OnGroupToggle(uiGroupModel.header.title)) },
                                )
                            }

                            if (isExpanded) {
                                items(
                                    uiGroupModel.listTransaction,
                                    key = { it.id ?: it.hashCode() },
                                ) { item ->
                                    Column {
                                        TransactionItem(
                                            transaction = item,
                                            onClick = {
                                                onEvent(
                                                    TransactionEvent.OnTransactionClick(
                                                        item.id,
                                                    ),
                                                )
                                            },
                                        )
                                        HorizontalDivider(color = BorderGray)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            MonthPicker(
                visible = state.showMonthPicker,
                currentMonth = state.selectedMonth.monthValue - 1,
                currentYear = state.selectedMonth.year,
                confirmButtonCLicked = { month, year ->
                    onEvent(TransactionEvent.OnMonthSelected(YearMonth.of(year, month + 1)))
                },
                cancelClicked = {
                    onEvent(TransactionEvent.OnMonthPickerDismiss)
                },
            )
        }
    }
}

@Composable
fun TransactionGroupHeader(
    label: String,
    count: Int,
    total: Long,
    isExpanded: Boolean,
    onToggle: () -> Unit,
) {
    val rotationState by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)

    Column(
        modifier = Modifier
                .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .clickable { onToggle() }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                        .background(
                            color = BorderGray.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(50.dp),
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                )
            }
            Spacer(Modifier.weight(1f))

            val isPositive = total >= 0
            Text(
                text = "${if (isPositive) "+" else "-"} Rp ${Math.abs(total).formatCurrency()}",
                color = if (isPositive) AccentTealGreen else Color.Red,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                modifier = Modifier
                        .padding(start = 4.dp)
                        .rotate(rotationState),
                tint = TextTertiary,
                contentDescription = null,
            )
        }
        Box(
            modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.05f),
                                Color.Transparent,
                            ),
                        ),
                    ),
        )
    }
}

@Composable
fun SummaryCard(
    title: String,
    amount: Long,
    modifier: Modifier,
    icon: ImageVector,
    iconColor: Color,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            PrimaryCyan,
        ),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundLight,
        ),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
        ) {
            IconCircular(
                icon = icon,
                iconColor = iconColor,
                backgroundColor = iconColor.copy(alpha = 0.1f),
                iconSize = 26.dp,
            )
            Text(
                modifier = Modifier.padding(vertical = 4.dp),
                text = title,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = stringResource(R.string.total_balance_format, amount.formatCompact()),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }

    }
}

val categoryList: List<CategoryNameItemUiModel> = listOf(
    CategoryNameItemUiModel(
        id = "Semua",
        nameCategory = "Semua",
    ),
    CategoryNameItemUiModel(
        id = "Makanan & Minuman",
        nameCategory = "Makanan & Minuman",
    ),
    CategoryNameItemUiModel(
        id = "Transportasi",
        nameCategory = "Transportasi",
    ),
    CategoryNameItemUiModel(
        id = "Belanja",
        nameCategory = "Belanja",
    ),
    CategoryNameItemUiModel(
        id = "Tagihan",
        nameCategory = "Tagihan",
    ),

    )


val groupedTransactions: List<TransactionUiGroupModel> = listOf(
    TransactionUiGroupModel(
        header = TransactionHeaderUiModel(
            id = "group_1",
            title = "Hari Ini, 17 Agustus 2026",
            time = "1755388800000L", // epoch millis untuk 17 Agu 2026
            totalAmount = 4_250_000L, // net (income - expense) grup ini
            isExpanded = true,
        ),
        listTransaction = listOf(
            TransactionItemUiModel(
                id = "trx_1",
                name = "Gaji Bulanan",
                time = "08:00",
                amount = 5_000_000L,
                typeTransaction = TransactionType.INCOME,
                category = "Gaji",
                account = "Bank BCA",
                icon = Icons.Default.AttachMoney,
                iconColor = PrimaryCyan,
            ),
            TransactionItemUiModel(
                id = "trx_2",
                name = "Makan Siang Warteg",
                time = "12:30",
                amount = 25_000L,
                typeTransaction = TransactionType.INCOME,
                category = "Makanan & Minuman",
                account = "Cash",
                icon = Icons.Default.Fastfood,
                iconColor = PrimaryCyan,

                ),
            TransactionItemUiModel(
                id = "trx_3",
                name = "Bensin Motor",
                time = "14:15",
                amount = 50_000L,
                typeTransaction = TransactionType.INCOME,
                category = "Transportasi",
                account = "Cash",
                icon = Icons.Default.Directions,
                iconColor = PrimaryCyan,

                ),
            TransactionItemUiModel(
                id = "trx_4",
                name = "Beli Baju",
                time = "16:45",
                amount = 275_000L,
                typeTransaction = TransactionType.INCOME,
                category = "Belanja",
                account = "Bank BCA",
                icon = Icons.Default.ShoppingBag,
                iconColor = PrimaryCyan,

                ),
            TransactionItemUiModel(
                id = "trx_5",
                name = "Tagihan Listrik",
                time = "19:00",
                amount = 400_000L,
                typeTransaction = TransactionType.INCOME,
                category = "Tagihan",
                account = "Bank BCA",
                icon = Icons.Default.Receipt,
                iconColor = PrimaryCyan,

                ),
        ),
    ),
    TransactionUiGroupModel(
        header = TransactionHeaderUiModel(
            id = "group_2",
            title = "Kemarin, 16 Agustus 2026",
            time = "1755302400000L", // epoch millis untuk 16 Agu 2026
            totalAmount = -180_000L,
            isExpanded = false,
        ),
        listTransaction = listOf(
            TransactionItemUiModel(
                id = "trx_6",
                name = "Nonton Bioskop",
                time = "20:00",
                amount = 100_000L,
                typeTransaction = TransactionType.INCOME,
                category = "Hiburan",
                account = "Cash",
                icon = Icons.Default.Movie,
                iconColor = PrimaryCyan,

                ),
            TransactionItemUiModel(
                id = "trx_7",
                name = "Beli Obat",
                time = "10:20",
                amount = 80_000L,
                typeTransaction = TransactionType.INCOME,
                category = "Kesehatan",
                account = "Cash",
                icon = Icons.Default.LocalHospital,
                iconColor = PrimaryCyan,

                ),
        ),
    ),
)

@Preview
@Composable
private fun TransactionScreenPreview() {
    NexCashApplicationTheme {
        TransactionContent(
            state = TransactionState(
                searchQuery = "",
                categoryList = categoryList,
                selectedCategoryId = "Semua",
                groupedTransactions = groupedTransactions,
                collapsGroups = setOf(groupedTransactions[0].header.title),
            ),
            onEvent = {},
        )
    }
}