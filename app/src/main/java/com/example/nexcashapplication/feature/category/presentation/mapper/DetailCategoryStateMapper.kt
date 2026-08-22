package com.example.nexcashapplication.feature.category.presentation.mapper

import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.feature.category.presentation.addnew.constant.CategoryIcons
import com.example.nexcashapplication.feature.category.presentation.detail.model.CategoryUiModel


fun Category.toUiModel(): CategoryUiModel = CategoryUiModel(
    id = id,
    name = name,
    icon = CategoryIcons.fromId(id = iconId),  // mapping enum -> resource
    totalBudget = budget,
    usedBudget = used,
    typeCategory = type,
)