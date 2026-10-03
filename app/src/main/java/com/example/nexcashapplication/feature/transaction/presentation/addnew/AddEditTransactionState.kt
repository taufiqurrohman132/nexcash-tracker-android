package com.example.nexcashapplication.feature.transaction.presentation.addnew

import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.account.presentation.main.model.AccountUiModel
import com.example.nexcashapplication.feature.transaction.presentation.addnew.model.TransactionCategoryAndSubModel
import com.example.nexcashapplication.feature.transaction.presentation.addnew.model.TransactionCategoryItemModel
import kotlin.time.Clock
import kotlin.time.Instant


data class AddEditTransactionState(
    val transactionId: String? = null,
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: Long = 0L,
    val nameTransaction: String = "",
    val selectedCategoryId: String? = null,
    val selectedAccountId: String? = null,
    val transactionAt: Instant = Clock.System.now(),
    val note: String = "",
    val attachmentName: String? = null,
    val isRecurring: Boolean = false,

    // mencegah Save dobel saat proses insert/update masih berjalan
    val isSaving: Boolean = false,

    val showDatePicker: Boolean = false,
    val showTimePicker: Boolean = false,

    val showCategoryBottomSheet: Boolean = false,
    val showAccountBottomSheet: Boolean = false,

    /** Daftar kategori sedang dimuat — sheet menampilkan indikator, bukan layar kosong. */
    val isCategoriesLoading: Boolean = false,

    val allCategories: List<TransactionCategoryAndSubModel> = emptyList(),
//    val availableSubCategories: List<TransactionCategoryItemModel> = emptyList(),
    val availableAccounts: List<AccountUiModel> = emptyList(),
) {
    // Used to enable/disable the Save button
    val isValid: Boolean
        get() = nameTransaction.isNotBlank() &&
                amount > 0L &&
                selectedCategoryId != null &&
                selectedAccountId != null

    /**
     * Kategori yang boleh dipilih untuk [type] sekarang — pengeluaran cuma boleh
     * memilih kategori expense, pemasukan cuma kategori income. Induk dan sub
     * kategori ikut difilter. Sumbernya [allCategories] (semua tipe), jadi ganti
     * tipe tidak perlu load ulang.
     */
    val availableCategories: List<TransactionCategoryAndSubModel>
        get() {
            val wanted = if (type == TransactionType.EXPENSE) {
                TypeCategory.EXPENSE
            } else {
                TypeCategory.INCOME
            }
            return allCategories.mapNotNull { group ->
                if (group.categoriItem.tipe != wanted) return@mapNotNull null
                group.copy(
                    subCategoriesItem = group.subCategoriesItem.filter { it.tipe == wanted },
                )
            }
        }

    /**
     * Grup induk dari [selectedCategoryId] — id terpilih boleh berupa sub kategori,
     * jadi dicari lewat induk **maupun** sub.
     *
     * Tanpa ini, sheet yang dibuka lagi setelah sub dipilih kehilangan sorotan
     * induk dan panel kanan balik ke placeholder "Pilih kategori utama".
     */
    val selectedParentGroupId: String?
        get() = availableCategories.find { group ->
            group.categoriItem.id == selectedCategoryId ||
                    group.subCategoriesItem.any { it.id == selectedCategoryId }
        }?.categoriItem?.id

    /** Id sub kategori terpilih — null kalau yang terpilih induk atau belum ada. */
    val selectedSubCategoryId: String?
        get() = selectedParentGroupId?.let { parent ->
            availableCategories.find { it.categoriItem.id == parent }
                    ?.subCategoriesItem
                    ?.find { it.id == selectedCategoryId }
                    ?.id
        }

    /**
     * Aturan tutup sheet Category: induk yang masih punya sub menahan sheet tetap
     * terbuka (pemilih harus memilih sub), selainnya sheet boleh ditutup.
     *
     * Satu sumber kebenaran untuk ViewModel **dan** screen — screen perlu tahu
     * aturan ini sebelum menjalankan animasi hide-nya sendiri.
     */
    fun keepsCategorySheetOpen(id: String): Boolean =
        availableCategories.any { it.categoriItem.id == id && it.subCategoriesItem.isNotEmpty() }

    val subCategories: List<TransactionCategoryItemModel>
        get() = availableCategories.find { it.categoriItem.id == selectedParentGroupId }
                ?.subCategoriesItem ?: emptyList()

    val selectedCategory: TransactionCategoryItemModel?
        get() = availableCategories.flatMap {
            listOf(it.categoriItem) + it.subCategoriesItem
        }.find { it.id == selectedCategoryId }

    val selectedAccount: AccountUiModel?
        get() = availableAccounts.find { it.id == selectedAccountId }
}

enum class TransactionType {
    INCOME,
    EXPENSE
}

