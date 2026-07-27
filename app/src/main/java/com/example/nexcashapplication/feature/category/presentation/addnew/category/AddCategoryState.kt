package com.example.nexcashapplication.feature.category.presentation.addnew.category

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.core.domain.model.CategoryIcon
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.category.presentation.addnew.category.model.BudgetPeriod
import com.example.nexcashapplication.ui.theme.PrimaryCyan

data class AddCategoryState(
    val id: String = "", // bukan form field, ini identitas — kosong = mode tambah baru
    val name: String = "",
    val typeCategory: TypeCategory = TypeCategory.EXPENSE,
//    val parentCategory: String? = null,
    val selectedColor: Color = PrimaryCyan,
    val recentColors: List<Color> = emptyList(),
    val budgetLimit: Long? = null,
    val selectedIcon: CategoryIcon? = null,
    val recentIcons: List<CategoryIcon> = emptyList(),
    val selectedPeriod: BudgetPeriod = BudgetPeriod.BULANAN,
    val defaultAccount: String = "Dompet Utama (Cash)",
    val note: String = "",
) {
    val isValid: Boolean
        get() = name.isNotBlank()
}
