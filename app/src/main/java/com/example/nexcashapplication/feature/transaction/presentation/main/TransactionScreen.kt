package com.example.nexcashapplication.feature.transaction.presentation.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nexcashapplication.core.component.IconCircular
import com.example.nexcashapplication.core.component.IconRounded
import com.example.nexcashapplication.core.component.TransactionItem
import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel
import com.example.nexcashapplication.core.extension.formatCompact
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.feature.transaction.presentation.main.model.TransactionHeaderUiModel
import com.example.nexcashapplication.feature.transaction.presentation.main.model.TransactionUiGroupModel
import com.example.nexcashapplication.feature.transaction.presentation.navigation.TransactionNavigationEvent
import com.example.nexcashapplication.ui.theme.AccentTealGreen
import com.example.nexcashapplication.ui.theme.BackgroundLight
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import com.example.nexcashapplication.ui.theme.SuccessGreen
import com.example.nexcashapplication.ui.theme.TextTertiary
import com.example.nexcashapplication.ui.theme.WarningAmber

@Composable
fun TransactionScreen(
    modifier: Modifier = Modifier,
    viewModel: TransactionViewModel = hiltViewModel(),
    onNavigateToAddTransaction: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { event ->
            when (event) {
                is TransactionNavigationEvent.NavigateToAddTransaction -> onNavigateToAddTransaction()
                is TransactionNavigationEvent.NavigateToDetail -> onNavigateToDetail(event.transactionId)
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
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconRounded(
                            imageVector = Icons.Outlined.Insights,
                            iconColor = Color.White,
                            backgroundColor = PrimaryCyan,
                            size = 28.dp,
                        )
                        Text(
                            text = "KeuanganKu",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                actions = {
                    IconButton(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onClick = { onEvent(TransactionEvent.OnTransactionClick("")) },
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Tambah akun")
                    }
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
                        .height(48.dp),
                shape = RoundedCornerShape(50.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = PrimaryCyan,
                    unfocusedIndicatorColor = TextTertiary,
                    unfocusedContainerColor = BackgroundLight,
                    focusedContainerColor = BackgroundLight,
                ),
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.caategoryList, key = { it }) { category ->
                    FilterChip(
                        selected = state.selectedCategory == category,
                        onClick = { onEvent(TransactionEvent.OnCategorySelected(category = category)) },
                        label = { Text(category) },
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
                state.groupedTransactions.forEach { uiGroupModel ->
                    stickyHeader(key = uiGroupModel.header.id) {
                        TransactionGroupHeader(
                            label = uiGroupModel.header.title,
                            count = uiGroupModel.listTransaction.size,
                            total = uiGroupModel.header.totalAmount,
                            isExpanded = state.expandedGroups.contains(uiGroupModel.header.title),
                            onToggle = { onEvent(TransactionEvent.OnGroupToggle(uiGroupModel.header.title)) },
                        )
                    }
                    items(
                        uiGroupModel.listTransaction,
                        key = { it.id },
                    ) { item ->

                        Column {
                            TransactionItem(
                                transaction = item,
                                onClick = { onEvent(TransactionEvent.OnTransactionClick(item.id)) },
                            )
                            HorizontalDivider(color = BorderGray)
                        }

                    }
                }

            }

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

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                    .fillMaxWidth()
//                .shadow(
//                    elevation = 12.dp,
//                    spotColor = Color.Black,
//                    ambientColor = Color.Black,
//
//                    )
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                        .background(
                            color = BorderGray,
                            shape = RoundedCornerShape(50.dp),
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Spacer(Modifier.weight(1f))

            Text(
                text = if (true)
                    "+ Rp ${total.formatCurrency()}"
                else
                    "- Rp ${total.formatCurrency()}",
                color = if (true) AccentTealGreen else Color.Red,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                modifier = Modifier.padding(start = 4.dp),
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
                                Color.Black.copy(alpha = 0.08f),
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
                size = 26.dp,
            )
            Text(
                modifier = Modifier.padding(vertical = 4.dp),
                text = title,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = amount.formatCompact(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }

    }
}

val categoryList: List<String> = listOf(
    "Semua",
    "Makanan & Minuman",
    "Transportasi",
    "Belanja",
    "Tagihan",
    "Hiburan",
    "Kesehatan",
    "Pendidikan",
    "Gaji",
    "Investasi",
    "Lainnya",
)


val groupedTransactions: List<TransactionUiGroupModel> = listOf(
    TransactionUiGroupModel(
        header = TransactionHeaderUiModel(
            id = "group_1",
            title = "Hari Ini, 17 Agustus 2026",
            time = 1755388800000L, // epoch millis untuk 17 Agu 2026
            totalAmount = 4_250_000L, // net (income - expense) grup ini
            isExpanded = true,
        ),
        listTransaction = listOf(
            TransactionItemUiModel(
                id = "trx_1",
                title = "Gaji Bulanan",
                time = "08:00",
                amount = 5_000_000L,
                isIncome = true,
                category = "Gaji",
                account = "Bank BCA",
                icon = Icons.Default.AttachMoney,
            ),
            TransactionItemUiModel(
                id = "trx_2",
                title = "Makan Siang Warteg",
                time = "12:30",
                amount = 25_000L,
                isIncome = false,
                category = "Makanan & Minuman",
                account = "Cash",
                icon = Icons.Default.Fastfood,
            ),
            TransactionItemUiModel(
                id = "trx_3",
                title = "Bensin Motor",
                time = "14:15",
                amount = 50_000L,
                isIncome = false,
                category = "Transportasi",
                account = "Cash",
                icon = Icons.Default.Directions,
            ),
            TransactionItemUiModel(
                id = "trx_4",
                title = "Beli Baju",
                time = "16:45",
                amount = 275_000L,
                isIncome = false,
                category = "Belanja",
                account = "Bank BCA",
                icon = Icons.Default.ShoppingBag,
            ),
            TransactionItemUiModel(
                id = "trx_5",
                title = "Tagihan Listrik",
                time = "19:00",
                amount = 400_000L,
                isIncome = false,
                category = "Tagihan",
                account = "Bank BCA",
                icon = Icons.Default.Receipt,
            ),
        ),
    ),
    TransactionUiGroupModel(
        header = TransactionHeaderUiModel(
            id = "group_2",
            title = "Kemarin, 16 Agustus 2026",
            time = 1755302400000L, // epoch millis untuk 16 Agu 2026
            totalAmount = -180_000L,
            isExpanded = false,
        ),
        listTransaction = listOf(
            TransactionItemUiModel(
                id = "trx_6",
                title = "Nonton Bioskop",
                time = "20:00",
                amount = 100_000L,
                isIncome = false,
                category = "Hiburan",
                account = "Cash",
                icon = Icons.Default.Movie,
            ),
            TransactionItemUiModel(
                id = "trx_7",
                title = "Beli Obat",
                time = "10:20",
                amount = 80_000L,
                isIncome = false,
                category = "Kesehatan",
                account = "Cash",
                icon = Icons.Default.LocalHospital,
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
                caategoryList = categoryList,
                selectedCategory = "Semua",
                groupedTransactions = groupedTransactions,
            ),
            onEvent = {},
        )
    }
}