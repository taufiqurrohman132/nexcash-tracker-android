//package com.example.nexcashapplication.feature.transaction.presentation.ui
//
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Clear
//import androidx.compose.material.icons.outlined.Search
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.FilterChip
//import androidx.compose.material3.FilterChipDefaults
//import androidx.compose.material3.FloatingActionButton
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextFieldColors
//import androidx.compose.material3.TextFieldDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.painter.Painter
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.example.nexcashapplication.R
//import com.example.nexcashapplication.core.component.IconCircular
//import com.example.nexcashapplication.core.extension.formatCompact
//import com.example.nexcashapplication.core.component.TransactionItem
//import com.example.nexcashapplication.feature.transaction.presentation.main.TransactionEvent
//import com.example.nexcashapplication.feature.transaction.presentation.main.TransactionState
//import com.example.nexcashapplication.feature.transaction.presentation.main.TransactionViewModel
//import com.example.nexcashapplication.ui.theme.BackgroundLight
//import com.example.nexcashapplication.ui.theme.BorderGray
//import com.example.nexcashapplication.ui.theme.PrimaryCyan
//import com.example.nexcashapplication.ui.theme.SuccessGreen
//import com.example.nexcashapplication.ui.theme.TextTertiary
//import com.example.nexcashapplication.ui.theme.WarningAmber
//
//@Composable
//fun TransactionScreen(
//    modifier: Modifier = Modifier,
//    viewModel: TransactionViewModel = hiltViewModel()
//) {
//    val state by viewModel.state.collectAsStateWithLifecycle()
//
//    TransactionContent(
//        state= state,
//        onEvent = viewModel::onEvent,
//        modifier= modifier
//    )
//
//}
//
//@Composable
//fun TransactionContent(
//    state: TransactionState,
//    onEvent: (TransactionEvent) -> Unit,
//    modifier: Modifier
//) {
//    Scaffold(
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = { onEvent(TransactionEvent.OnAddTransactionClick) },
//                containerColor = PrimaryCyan,
//                shape = CircleShape
//            ) {
//                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
//            }
//        }
//    ) {padding ->
//        LazyColumn(
//            modifier = modifier
//                .fillMaxSize()
//                .padding(padding),
//            verticalArrangement = Arrangement.spacedBy(0.dp)
//        ) {
//            item(key = "search_bar"){
//                OutlinedTextField(
//                    value = state.searchQuery,
//                    onValueChange = { onEvent(TransactionEvent.OnSearchQueryChange(it))},
//                    placeholder = { Text("Cari Transaksi...") },
//                    leadingIcon = {
//                        Icon(Icons.Outlined.Search, contentDescription = null)
//                    },
//                    trailingIcon = {
//                        if (state.searchQuery.isNotEmpty()){
//                            IconButton(
//                                modifier = Modifier.padding(horizontal = 4.dp),
//                                onClick = {
//                                    onEvent(TransactionEvent.OnSearchClear)
//                                }
//                            ) {
//                                Icon(Icons.Default.Clear, contentDescription = null)
//                            }
//                        }
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 16.dp, vertical = 8.dp)
//                        .height(48.dp),
//                    shape = RoundedCornerShape(50.dp),
//                    singleLine = true,
//                    colors = TextFieldDefaults.colors(
//                        focusedIndicatorColor = PrimaryCyan,
//                        unfocusedIndicatorColor = TextTertiary,
//                        unfocusedContainerColor = BackgroundLight,
//                        focusedContainerColor = BackgroundLight
//                    )
//                )
//            }
//
//            // filter category
//            item (key = "filter_category"){
//                LazyRow(
//                    contentPadding = PaddingValues(horizontal = 16.dp),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    items(state.caategoryList, key = {it}){ category ->
//                        FilterChip(
//                            selected = state.selectedCategory == category,
//                            onClick = { onEvent(TransactionEvent.OnCategorySelected(category = category))},
//                            label = {Text(category)},
//                            colors = FilterChipDefaults.filterChipColors(
//                                selectedLabelColor = Color.White,
//                                selectedContainerColor = PrimaryCyan
//                            ),
//                            shape = RoundedCornerShape(50.dp)
//                        )
//                    }
//                }
//            }
//
//            // sumary card
//            item(key= "summary_card"){
//                Row(
//                    modifier = Modifier.fillMaxWidth()
//                        .padding(16.dp),
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    SummaryCard(
//                        title= "Hari Ini",
//                        amount= state.todayTotal,
//                        modifier= Modifier.weight(1f),
//                        iconPainter = painterResource(R.drawable.down),
//                        iconColor = Color.Red
//                    )
//                    SummaryCard(
//                        title= "Minggu Ini",
//                        amount= state.weekTotal,
//                        modifier= Modifier.weight(1f),
//                        iconPainter = painterResource(R.drawable.insights),
//                        iconColor = WarningAmber,
//
//                    )
//                    SummaryCard(
//                        title= "Sisa Budget",
//                        amount= state.remainingBudget,
//                        modifier= Modifier.weight(1f),
//                        iconColor = SuccessGreen,
//                        iconPainter = painterResource(R.drawable.scan)
//                    )
//                }
//            }
//
//            // group transaction
//            state.groupedTransactions.forEach { (groupLabel, transactions) ->
//                item(key = "header_$groupLabel"){
//                    TransactionGroupHeader(
//                        label= groupLabel,
//                        count= transactions.size,
//                        total= transactions.sumOf { if (it.isIncome) it.amount else -it.amount},
//                        isExpanded = state.expandedGroups.contains(groupLabel),
//                        onToggle = {onEvent(TransactionEvent.OnGroupToggle(groupLabel))}
//                    )
//                }
//                // Item transaksi (hanya tampil kalau expanded)
//                if (state.expandedGroups.contains(groupLabel)){
//                    items(
//                        items = transactions,
//                        key = {it.id}
//                    ){ transaction ->
//                        TransactionItem(
//                            transaction= transaction,
//                            onClick = { onEvent(TransactionEvent.OnTransactionClick(transaction.id))}
//                        )
//                        HorizontalDivider(
//                            modifier = Modifier.padding(horizontal = 16.dp),
//                            color = BorderGray
//                        )
//                    }
//                }
//
//            }
//        }
//
//
//    }
//}
//
//@Composable
//fun TransactionGroupHeader(
//    label: String,
//    count: Int,
//    total: Long,
//    isExpanded: Boolean,
//    onToggle: () -> Unit
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(
//            text = label,
//            style = MaterialTheme.typography.titleMedium,
//        )
//
//        Spacer(modifier = Modifier.width(16.dp))
//
//        Box(
//            modifier = Modifier
//                .background(
//                    color = BorderGray,
//                    shape = RoundedCornerShape(50.dp)
//                )
//                .padding(horizontal = 8.dp, vertical = 2.dp)
//        ){
//            Text(
//                text = "$count",
//                style = MaterialTheme.typography.bodySmall
//            )
//        }
//        Spacer(Modifier.weight(1f))
//
//        IconButton(
//            modifier = Modifier.padding(4.dp),
//            onClick = onToggle
//        ) {
//            Icon(
//                painter = painterResource(
//                    if (isExpanded)
//                        R.drawable.up
//                    else R.drawable.down
//                ),
//                modifier = Modifier.size(12.dp),
//                contentDescription = null
//            )
//        }
//
//
//    }
//}
//
//@Composable
//fun SummaryCard(
//    title: String,
//    amount: Long,
//    modifier: Modifier,
//    iconPainter: Painter,
//    iconColor: Color
//) {
//    Card(
//        modifier= modifier,
//        shape = RoundedCornerShape(16.dp),
//        border= BorderStroke(
//            1.dp,
//            PrimaryCyan
//        ),
//        colors = CardDefaults.cardColors(
//            containerColor = BackgroundLight
//        )
//    ) {
//        Column(
//            modifier = Modifier.padding(10.dp)
//        ){
//            IconCircular(
//                painter = iconPainter,
//                iconColor = iconColor,
//                backgroundColor = iconColor.copy(alpha = 0.1f),
//                size = 26.dp
//            )
//            Text(
//                modifier = Modifier.padding(vertical = 4.dp),
//                text = title,
//                style = MaterialTheme.typography.bodySmall
//            )
//            Text(
//                text = amount.formatCompact(),
//                style = MaterialTheme.typography.titleLarge,
//                fontWeight = FontWeight.SemiBold
//            )
//        }
//
//    }
//}
//
//@Preview
//@Composable
//private fun TransactionScreenPreview() {
//    TransactionScreen()
//}