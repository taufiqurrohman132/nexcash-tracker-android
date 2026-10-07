package com.example.nexcashapplication.feature.transaction.presentation.addnew.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Icecream
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.domain.model.CategoryIcon
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.transaction.presentation.addnew.model.TransactionCategoryItemModel
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme

@Composable
fun CategoryBottomSheetContent(
    parentCategories: List<TransactionCategoryItemModel>,
    subCategories: List<TransactionCategoryItemModel>,
    selectedParentId: String? = null,
    selectedSubId: String? = null,
    isLoading: Boolean = false,
    onCategorySelected: (TransactionCategoryItemModel) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
            .padding(vertical = 16.dp, horizontal = 8.dp),
    ) {
        // heading: judul dibaca screen reader saat sheet dibuka
        Text(
            text = stringResource(R.string.tx_select_category),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 12.dp),
        )

        when {
            // daftar belum termuat → indikator, bukan dua kolom kosong diam
            isLoading -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            parentCategories.isEmpty() -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.tx_no_categories),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            else -> Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1.2f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(parentCategories, key = { it.id }) { category ->
                        ParentCategoryItem(
                            category = category,
                            isSelected = category.id == selectedParentId,
                            onClick = {
                                onCategorySelected(category)
                            },
                        )
                    }
                }

                VerticalDivider(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxHeight(),
                    thickness = 1.dp,
                    color = BorderGray,
                )

                if (subCategories.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(R.string.tx_pick_parent_category),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.weight(1.8f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(subCategories, key = { it.id }) { category ->
                            SubCategoryItem(
                                category = category,
                                isSelected = category.id == selectedSubId,
                                onClick = {
                                    onCategorySelected(category)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ParentCategoryItem(
    category: TransactionCategoryItemModel,
    isSelected: Boolean,
    onClick: (TransactionCategoryItemModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
            // selectable, bukan clickable: screen reader mengumumkan "terpilih"
            .selectable(
                selected = isSelected,
                role = Role.Button,
                onClick = { onClick(category) },
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = category.icon.icon,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else category.iconColor,
            contentDescription = null,
        )
        Text(
            category.name,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Icon(
            imageVector = Icons.Default.NavigateNext,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else BorderGray,
            contentDescription = null,
        )
    }
}


@Composable
fun SubCategoryItem(
    category: TransactionCategoryItemModel,
    isSelected: Boolean = false,
    onClick: (TransactionCategoryItemModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                },
            )
            // selectable, bukan clickable: screen reader mengumumkan "terpilih"
            .selectable(
                selected = isSelected,
                role = Role.Button,
                onClick = { onClick(category) },
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            modifier = Modifier.size(28.dp),
            imageVector = category.icon.icon,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else category.iconColor,
            contentDescription = null,
        )
        Text(
            category.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ParentCategoryItemPreview() {
    NexCashApplicationTheme {
        ParentCategoryItem(
            category = TransactionCategoryItemModel(
                id = "kdf",
                name = "Liburan",
                tipe = TypeCategory.EXPENSE,
                icon = CategoryIcon(
                    id = "kdf",
                    icon = Icons.Default.Shop,
                ),
                iconColor = Color.Green,
            ),
            isSelected = true,
            onClick = {

            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SubCategoryItemPreview() {
    NexCashApplicationTheme {
        SubCategoryItem(
            category = TransactionCategoryItemModel(
                id = "kdf",
                name = "Liburan",
                tipe = TypeCategory.EXPENSE,
                icon = CategoryIcon(
                    id = "kdf",
                    icon = Icons.Default.Shop,
                ),
                iconColor = Color.Green,
            ),
            isSelected = true,
            onClick = {

            },
        )
    }
}

// ---------------------------------------------------------------------------
// PREVIEWS — CategoryBottomSheetContent (satu untuk tiap cabang `when`)
// ---------------------------------------------------------------------------
// Konten ber-height(450.dp) fixed, jadi preview pakai heightDp 500 supaya tidak
// terpotong; widthDp 360 supaya grid 2 kolom muat seperti di perangkat nyata.

private fun sampleParentCategories() = listOf(
    TransactionCategoryItemModel(
        id = "makanan",
        name = "Makanan & Minuman",
        tipe = TypeCategory.EXPENSE,
        icon = CategoryIcon("restaurant", Icons.Default.Restaurant),
        iconColor = Color(0xFFE8433D),
    ),
    TransactionCategoryItemModel(
        id = "transport",
        name = "Transportasi",
        tipe = TypeCategory.EXPENSE,
        icon = CategoryIcon("car", Icons.Default.DirectionsCar),
        iconColor = Color(0xFF1E88E5),
    ),
    TransactionCategoryItemModel(
        id = "belanja",
        name = "Belanja",
        tipe = TypeCategory.EXPENSE,
        icon = CategoryIcon("cart", Icons.Default.ShoppingCart),
        iconColor = Color(0xFFFB8C00),
    ),
    TransactionCategoryItemModel(
        id = "rumah",
        name = "Kebutuhan Rumah",
        tipe = TypeCategory.EXPENSE,
        icon = CategoryIcon("home", Icons.Default.Home),
        iconColor = Color(0xFF43A047),
    ),
    TransactionCategoryItemModel(
        id = "hiburan",
        name = "Hiburan",
        tipe = TypeCategory.EXPENSE,
        icon = CategoryIcon("movie", Icons.Default.Movie),
        iconColor = Color(0xFF8E24AA),
    ),
    TransactionCategoryItemModel(
        id = "kesehatan",
        name = "Kesehatan",
        tipe = TypeCategory.EXPENSE,
        icon = CategoryIcon("health", Icons.Default.LocalHospital),
        iconColor = Color(0xFF00ACC1),
    ),
)

private fun sampleSubCategories() = listOf(
    TransactionCategoryItemModel(
        id = "sarapan",
        name = "Sarapan",
        tipe = TypeCategory.EXPENSE,
        icon = CategoryIcon("fastfood", Icons.Default.Fastfood),
        iconColor = Color(0xFFE8433D),
    ),
    TransactionCategoryItemModel(
        id = "kopi",
        name = "Kopi",
        tipe = TypeCategory.EXPENSE,
        icon = CategoryIcon("coffee", Icons.Default.Coffee),
        iconColor = Color(0xFF795548),
    ),
    TransactionCategoryItemModel(
        id = "gorengan",
        name = "Gorengan",
        tipe = TypeCategory.EXPENSE,
        icon = CategoryIcon("restaurant", Icons.Default.Restaurant),
        iconColor = Color(0xFFFDD835),
    ),
    TransactionCategoryItemModel(
        id = "dessert",
        name = "Dessert",
        tipe = TypeCategory.EXPENSE,
        icon = CategoryIcon("icecream", Icons.Default.Icecream),
        iconColor = Color(0xFFEC407A),
    ),
)

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 500,
    name = "Content - Parent terpilih, belum ada sub",
)
@Composable
private fun CategoryBottomSheetParentSelectedPreview() {
    NexCashApplicationTheme {
        CategoryBottomSheetContent(
            parentCategories = sampleParentCategories(),
            subCategories = emptyList(),
            selectedParentId = "makanan",
            onCategorySelected = {},
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 500,
    name = "Content - Sub terpilih",
)
@Composable
private fun CategoryBottomSheetSubSelectedPreview() {
    NexCashApplicationTheme {
        CategoryBottomSheetContent(
            parentCategories = sampleParentCategories(),
            subCategories = sampleSubCategories(),
            selectedParentId = "makanan",
            selectedSubId = "kopi",
            onCategorySelected = {},
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 500,
    name = "Content - Loading",
)
@Composable
private fun CategoryBottomSheetLoadingPreview() {
    NexCashApplicationTheme {
        CategoryBottomSheetContent(
            parentCategories = emptyList(),
            subCategories = emptyList(),
            isLoading = true,
            onCategorySelected = {},
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 500,
    name = "Content - Empty",
)
@Composable
private fun CategoryBottomSheetEmptyPreview() {
    NexCashApplicationTheme {
        CategoryBottomSheetContent(
            parentCategories = emptyList(),
            subCategories = emptyList(),
            onCategorySelected = {},
        )
    }
}
