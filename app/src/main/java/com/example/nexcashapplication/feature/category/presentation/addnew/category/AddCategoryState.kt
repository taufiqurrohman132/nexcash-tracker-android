package com.example.nexcashapplication.feature.category.presentation.addnew.category

data class AddCategoryState(
    val name: String = "",
    val isExpense: Boolean = true,
//    val parentCategory: String? = null,
    val selectedColorIndex: Int = 0,
    val budgetLimit: String = "",
    val frequency: String = "Bulanan",
    val defaultAccount: String = "Dompet Utama (Cash)",
    val note: String = ""
) {
    val isValid: Boolean
        get() = name.isNotBlank()
}
