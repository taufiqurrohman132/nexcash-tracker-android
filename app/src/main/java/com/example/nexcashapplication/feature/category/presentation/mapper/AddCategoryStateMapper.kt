package com.example.nexcashapplication.feature.category.presentation.mapper

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt
import com.example.nexcashapplication.core.domain.model.CategoryIcon
import com.example.nexcashapplication.feature.category.domain.model.Category
import com.example.nexcashapplication.feature.category.presentation.addnew.category.AddCategoryState
import com.example.nexcashapplication.feature.category.presentation.addnew.category.constant.CategoryIcons


fun AddCategoryState.toDomain(id: String): Category {

    return Category(
        name = name,
        type = typeCategory,
        icon = selectedIcon?.id.orEmpty(),
        color = selectedColor.toString(),
        used = 0L,
        budget = budgetLimit ?: 0L,
        period = selectedPeriod,
        defaultAccount = defaultAccount,
        note = note,
    )
}

fun Category.toUiState(
    recentColors: List<Color> = emptyList(),
    recentIcons: List<CategoryIcon> = emptyList(),
): AddCategoryState {
    val matchedIcon = recentIcons.find { it.id == icon }
    val matchedColor = recentColors.find { it.toArgb() == color.toColorInt() }

    return AddCategoryState(
        id = id,
        name = name,
        typeCategory = type,
        selectedIcon = matchedIcon,
        selectedColor = matchedColor ?: Color.Black,
        recentIcons = recentIcons,
        recentColors = recentColors,
        budgetLimit = budget,
        selectedPeriod = period,
        defaultAccount = defaultAccount,
        note = note,
    )
}

val default: CategoryIcon = CategoryIcons.lainnya.last() // "more_horiz" sebagai fallback
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
//
//
//fun String.toIconColor(): Color = when (this) {
//    "makanan" -> Color(0xFFFFE0CC)
//    "transportasi" -> Color(0xFFD9E4FF)
//    "belanja" -> Color(0xFFFFD9E8)
//    "kesehatan" -> Color(0xFFD4F5E0)
//    "tagihan" -> Color(0xFFFFF3D0)
//    "hiburan" -> Color(0xFFEBDCFF)
//    "investasi" -> Color(0xFFD4EAFF)
//    else -> Color(0xFFEDEDED)
//}
