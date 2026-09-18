package com.example.nexcashapplication.feature.category.presentation.mapper

import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.feature.category.presentation.addnew.constant.CategoryIcons
import com.example.nexcashapplication.feature.category.presentation.shared.model.CategoryUiItemModel
import com.example.nexcashapplication.feature.common.extentions.toColor


fun Category.toUiModel(): CategoryUiItemModel =
    CategoryUiItemModel(
        id = id,
        parentId = parentId,
        name = name,
        icon = CategoryIcons.fromId(id = iconId),  // mapping enum -> resource
        typeCategory = type,
        color = color.toColor(),
    )