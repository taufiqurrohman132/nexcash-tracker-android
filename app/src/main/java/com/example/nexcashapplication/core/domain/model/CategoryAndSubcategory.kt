package com.example.nexcashapplication.core.domain.model

data class CategoryAndSubcategory(
    val category: Category,
    val subCategories: List<Category>,
)
