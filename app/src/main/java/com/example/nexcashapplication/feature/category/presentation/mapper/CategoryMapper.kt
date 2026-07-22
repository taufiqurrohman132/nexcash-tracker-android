package com.example.nexcashapplication.feature.category.presentation.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.nexcashapplication.core.datasource.local.model.CategoryEntity
import com.example.nexcashapplication.feature.category.presentation.main.model.CategoryFilter
import com.example.nexcashapplication.feature.category.presentation.main.model.CategoryItem

// mapper — taruh di file terpisah biar rapi, misal ui/kategori/mapper/KategoriMapper.kt
fun CategoryEntity.toUiModel(): CategoryItem = CategoryItem(
    id = id,
    nama = name,
    tipe = CategoryFilter.valueOf(type.uppercase()),
    icon = icon.toIconVector(),
    iconBgColor = icon.toIconColor(),
    terpakai = used,
    budget = budget
)

fun CategoryItem.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = nama,
    type = tipe.name.lowercase(),
    icon = icon.toIconKey(), // reverse mapping, atau simpan iconKey terpisah di form input
    used = terpakai,
    color = "",
    budget = budget
)
// helper mapping icon key <-> ImageVector
fun String.toIconVector(): ImageVector = when (this) {
    "makanan" -> Icons.Default.ShoppingCart
    "transportasi" -> Icons.Default.LocationOn
    "belanja" -> Icons.Default.Star
    "kesehatan" -> Icons.Default.Favorite
    "tagihan" -> Icons.Default.Warning
    "hiburan" -> Icons.Default.PlayArrow
    "investasi" -> Icons.Default.Info
    else -> Icons.Default.MoreVert
}

fun ImageVector.toIconKey(): String = when (this) {
    Icons.Default.ShoppingCart -> "makanan"
    Icons.Default.LocationOn -> "transportasi"
    Icons.Default.Star -> "belanja"
    Icons.Default.Favorite -> "kesehatan"
    Icons.Default.Warning -> "tagihan"
    Icons.Default.PlayArrow -> "hiburan"
    Icons.Default.Info -> "investasi"
    else -> "lainnya"
}


fun String.toIconColor(): Color = when (this) {
    "makanan" -> Color(0xFFFFE0CC)
    "transportasi" -> Color(0xFFD9E4FF)
    "belanja" -> Color(0xFFFFD9E8)
    "kesehatan" -> Color(0xFFD4F5E0)
    "tagihan" -> Color(0xFFFFF3D0)
    "hiburan" -> Color(0xFFEBDCFF)
    "investasi" -> Color(0xFFD4EAFF)
    else -> Color(0xFFEDEDED)
}
