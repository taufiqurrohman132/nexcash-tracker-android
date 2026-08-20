package com.example.nexcashapplication.core.datasource.local.mapper

import com.example.nexcashapplication.core.datasource.local.model.CategoryAndSubcategoryEntity
import com.example.nexcashapplication.core.domain.model.CategoryAndSubcategory

fun CategoryAndSubcategoryEntity.toDomain() = CategoryAndSubcategory(
    category = category.toDomain(),
    subCategories = subCategories.map { it.toDomain() },
)
