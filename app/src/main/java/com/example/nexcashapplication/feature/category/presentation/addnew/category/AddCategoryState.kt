package com.example.nexcashapplication.feature.category.presentation.addnew.category

import com.example.nexcashapplication.feature.category.presentation.addnew.category.model.BudgetPeriod
import com.example.nexcashapplication.core.domain.model.TypeCategory

data class AddCategoryState(
    val name: String = "",
    val typeCategory: TypeCategory = TypeCategory.EXPANSE,
//    val parentCategory: String? = null,
    val selectedColorIndex: Int = 0,
    val budgetLimit: String = "",
    val selectedPeriod: BudgetPeriod = BudgetPeriod.BULANAN,
    val defaultAccount: String = "Dompet Utama (Cash)",
    val note: String = ""
) {
    val isValid: Boolean
        get() = name.isNotBlank()
}
