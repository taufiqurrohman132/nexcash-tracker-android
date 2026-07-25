package com.example.nexcashapplication.feature.category.presentation.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.nexcashapplication.core.datasource.local.model.CategoryEntity
import com.example.nexcashapplication.core.domain.model.CategoryIcon
import com.example.nexcashapplication.core.domain.model.DummyCategoryIcons
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.category.domain.model.Category


fun CategoryEntity.toDomain() = Category(
    id = id,
    parentId = parentId,
    name = name,
    type = TypeCategory.valueOf(type.uppercase()),
    icon = icon,
    color = color,
    used = used,
    budget = budget,
    period = period,
    isSystem = isSystem,
    deletedAt = deletedAt,
)

fun Category.toEntity() = CategoryEntity(
    id = id,
    parentId = parentId,
    name = name,
    type = type.name.lowercase(), // sesuai komentar entity: "expense / income" (lowercase)
    icon = icon,
    color = color,
    used = used,
    period = period,
    budget = budget,
    isSystem = isSystem,
    deletedAt = deletedAt ?: 0L, // sesuaikan sama default Entity — lihat catatan di bawah
)

val default: CategoryIcon = DummyCategoryIcons.lainnya.last() // "more_horiz" sebagai fallback

// helper mapping icon key <-> ImageVector
fun String.toIconVector(): CategoryIcon = DummyCategoryIcons.allGroups
        .flatMap { it.icons }
        .find { it.id == this } ?: default

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
