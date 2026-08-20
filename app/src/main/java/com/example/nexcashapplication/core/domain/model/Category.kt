package com.example.nexcashapplication.core.domain.model

import com.example.nexcashapplication.feature.category.presentation.addnew.model.BudgetPeriod

data class Category(
    val id: String = "",
    val parentId: String? = null,
    val name: String,
    val type: TypeCategory,
    val iconId: String,
    val color: String,
    val used: Long,
    val budget: Long,
    val period: BudgetPeriod,        // <- ganti dari String ke enum
    val defaultAccount: String,
    val note: String = "",
    val isSystem: Boolean = false,
    val deletedAt: Long? = null,
)