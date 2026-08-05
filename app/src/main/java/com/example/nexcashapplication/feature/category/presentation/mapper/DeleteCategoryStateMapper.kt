package com.example.nexcashapplication.feature.category.presentation.mapper

import com.example.nexcashapplication.core.extension.toColor
import com.example.nexcashapplication.feature.category.domain.model.CategoryName
import com.example.nexcashapplication.feature.category.presentation.addnew.constant.CategoryIcons
import com.example.nexcashapplication.feature.category.presentation.delete.model.CategoryDeleteItemUI


fun CategoryName.toDeleteUiModel(): CategoryDeleteItemUI = CategoryDeleteItemUI(
    id = id,
    name = name,
    icon = CategoryIcons.fromId(id = icon),  // mapping enum -> resource
    iconColor = color.toColor(),
)

fun List<CategoryName>.toCategoryDeleteItemList(): List<CategoryDeleteItemUI> =
    map { it.toDeleteUiModel() }

