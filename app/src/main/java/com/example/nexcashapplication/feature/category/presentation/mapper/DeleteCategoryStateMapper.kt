package com.example.nexcashapplication.feature.category.presentation.mapper

import com.example.nexcashapplication.feature.category.domain.model.CategoryName
import com.example.nexcashapplication.feature.category.presentation.addnew.constant.CategoryIcons
import com.example.nexcashapplication.feature.category.presentation.delete.model.CategoryDeleteItemUI
import com.example.nexcashapplication.feature.common.extentions.toColor


fun CategoryName.toDeleteUiModel(): CategoryDeleteItemUI = CategoryDeleteItemUI(
    id = id,
    name = name,
    icon = CategoryIcons.fromId(id = icon),  // mapping enum -> resource
    iconColor = color.toColor(),
)

fun List<CategoryName>.toCategoryDeleteItemList(): List<CategoryDeleteItemUI> =
    map { it.toDeleteUiModel() }

