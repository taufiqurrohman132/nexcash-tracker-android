package com.example.nexcashapplication.feature.category.data.mapper

import com.example.nexcashapplication.core.datasource.local.model.CategoryEntity
import com.example.nexcashapplication.core.datasource.local.result.CategoryNameResult
import com.example.nexcashapplication.core.domain.model.CategoryIcon
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.category.domain.model.Category
import com.example.nexcashapplication.feature.category.domain.model.CategoryName
import com.example.nexcashapplication.feature.category.presentation.addnew.category.constant.CategoryIcons
import com.example.nexcashapplication.feature.category.presentation.addnew.category.model.BudgetPeriod
import java.util.UUID


fun CategoryEntity.toDomain() = Category(
    id = id,
    parentId = parentId,
    name = name,
    type = TypeCategory.valueOf(type.uppercase()),
    icon = icon,
    color = color,
    used = used,
    budget = budget,
    period = BudgetPeriod.valueOf(period.uppercase()),
    defaultAccount = defaultAccount,
    note = note,
    isSystem = isSystem,
    deletedAt = deletedAt,
)

fun Category.toEntity() = CategoryEntity(
    id = id.ifBlank { UUID.randomUUID().toString() },
    parentId = parentId,
    name = name,
    type = type.name.lowercase(),
    icon = icon,
    color = color,
    used = used,
    budget = budget,
    period = period.name.lowercase(),
    defaultAccount = defaultAccount,
    note = note,
    isSystem = isSystem,
    deletedAt = deletedAt,
)

val default: CategoryIcon = CategoryIcons.lainnya.last() // "more_horiz" sebagai fallback

fun CategoryNameResult.toDomain() = CategoryName(
    id = categoryId,
    name = name,
    icon = icon,
    color = color,
)
//
//// helper mapping icon key <-> ImageVector
//fun String.toIconVector(): CategoryIcon = CategoryIcons.allGroups
//        .flatMap { it.icons }
//        .find { it.id == this } ?: default
//
//fun ImageVector.toIconKey(): String = when (this) {
//    Icons.Default.ShoppingCart -> "makanan"
//    Icons.Default.LocationOn -> "transportasi"
//    Icons.Default.Star -> "belanja"
//    Icons.Default.Favorite -> "kesehatan"
//    Icons.Default.Warning -> "tagihan"
//    Icons.Default.PlayArrow -> "hiburan"
//    Icons.Default.Info -> "investasi"
//    else -> "lainnya"
//}

