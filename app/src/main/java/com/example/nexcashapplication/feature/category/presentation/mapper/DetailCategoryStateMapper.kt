package com.example.nexcashapplication.feature.category.presentation.mapper

import com.example.nexcashapplication.feature.category.domain.model.Category
import com.example.nexcashapplication.feature.category.presentation.addnew.constant.CategoryIcons
import com.example.nexcashapplication.feature.category.presentation.detail.model.CategoryDetail


fun Category.toUiModel(): CategoryDetail = CategoryDetail(
    id = id,
    name = name,
    icon = CategoryIcons.fromId(id = icon),  // mapping enum -> resource
    totalBudget = budget,
    usedBudget = used,
    typeCategory = type,
)