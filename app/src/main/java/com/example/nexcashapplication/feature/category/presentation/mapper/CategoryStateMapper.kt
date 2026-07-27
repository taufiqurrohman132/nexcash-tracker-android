package com.example.nexcashapplication.feature.category.presentation.mapper

import com.example.nexcashapplication.core.extension.toColor
import com.example.nexcashapplication.feature.category.domain.model.Category
import com.example.nexcashapplication.feature.category.presentation.addnew.category.constant.CategoryIcons
import com.example.nexcashapplication.feature.category.presentation.main.model.CategoryItem

fun Category.toCategoryItem(): CategoryItem {
    return CategoryItem(
        id = id.toLongOrNull() ?: 0L, // lihat catatan soal id di bawah
        nama = name,
        tipe = type,
        icon = CategoryIcons.fromId(id = icon),
        iconColor = color.toColor(),
        terpakai = used,
        budget = budget,
    )
}

fun List<Category>.toCategoryItemList(): List<CategoryItem> = map { it.toCategoryItem() }