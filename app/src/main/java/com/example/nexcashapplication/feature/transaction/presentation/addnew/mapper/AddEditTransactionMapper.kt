package com.example.nexcashapplication.feature.transaction.presentation.addnew.mapper

import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.CategoryAndSubcategory
import com.example.nexcashapplication.core.extension.toColor
import com.example.nexcashapplication.feature.category.presentation.addnew.constant.CategoryIcons
import com.example.nexcashapplication.feature.transaction.presentation.addnew.model.TransactionCategoryAndSubModel
import com.example.nexcashapplication.feature.transaction.presentation.addnew.model.TransactionCategoryItemModel

fun Category.toTransactionCategoryItemModel() = TransactionCategoryItemModel(
    id = id,
    name = name,
    tipe = type,
    icon = CategoryIcons.fromId(iconId),
    iconColor = color.toColor(),
)

fun CategoryAndSubcategory.toUIModel() = TransactionCategoryAndSubModel(
    categoriItem = category.toTransactionCategoryItemModel(),
    subCategoriesItem = subCategories.map { it.toTransactionCategoryItemModel() },
)