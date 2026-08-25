package com.example.nexcashapplication.feature.category.presentation.mapper

import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.feature.category.presentation.addnew.constant.CategoryIcons
import com.example.nexcashapplication.feature.category.presentation.main.model.CategoryItem
import com.example.nexcashapplication.feature.common.extentions.toColor
import com.example.nexcashapplication.feature.transaction.presentation.main.model.CategoryNameItemUiModel

fun Category.toCategoryItem(): CategoryItem {
    return CategoryItem(
        id = id, // lihat catatan soal id di bawah
        nama = name,
        tipe = type,
        icon = CategoryIcons.fromId(id = iconId),
        iconColor = color.toColor(),
        terpakai = used,
        budget = budget,
    )
}

fun List<Category>.toCategoryItemList(): List<CategoryItem> = map { it.toCategoryItem() }

fun Category.toCategoryNameItemUiModel() = CategoryNameItemUiModel(
    id = id,
    nameCategory = name,
)