package com.example.nexcashapplication.feature.transaction.presentation.addnew.mapper

import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.CategoryAndSubcategory
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.account.presentation.main.model.AccountUiModel
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import kotlin.time.Instant

class AddEditTransactionMapperTest {

    @Test
    fun `toTransactionCategoryItemModel memetakan Category ke TransactionCategoryItemModel`() {
        val category = Category(
            id = "cat-1",
            name = "Kopi & Minuman",
            type = TypeCategory.EXPENSE,
            iconId = "coffee",
            color = "#FF9800",
        )

        val itemModel = category.toTransactionCategoryItemModel()

        assertEquals("cat-1", itemModel.id)
        assertEquals("Kopi & Minuman", itemModel.name)
        assertEquals(TypeCategory.EXPENSE, itemModel.tipe)
        assertNotNull(itemModel.icon)
    }

    @Test
    fun `toUIModel memetakan CategoryAndSubcategory ke TransactionCategoryAndSubModel`() {
        val parentCat = Category(
            id = "parent-1",
            name = "Makanan & Minuman",
            type = TypeCategory.EXPENSE,
            iconId = "restaurant",
            color = "#E91E63",
        )
        val subCat = Category(
            id = "sub-1",
            name = "Camilan",
            type = TypeCategory.EXPENSE,
            iconId = "fastfood",
            color = "#FF5722",
            parentId = "parent-1",
        )
        val data = CategoryAndSubcategory(
            category = parentCat,
            subCategories = listOf(subCat),
        )

        val uiModel = data.toUIModel()

        assertEquals("parent-1", uiModel.categoriItem.id)
        assertEquals("Makanan & Minuman", uiModel.categoriItem.name)
        assertEquals(1, uiModel.subCategoriesItem.size)
        assertEquals("sub-1", uiModel.subCategoriesItem[0].id)
        assertEquals("Camilan", uiModel.subCategoriesItem[0].name)
    }

    @Test
    fun `toAddEditTransactionState memetakan TransactionWithDetails ke AddEditTransactionState`() {
        val instant = Instant.fromEpochMilliseconds(1_700_000_000_000L)
        val tx = Transaction(
            id = "tx-100",
            amount = 85_000L,
            title = "Belanja",
            categoryId = "cat-1",
            description = "Catatan belanja",
            dateTime = instant,
            typeTransaction = TypeCategory.EXPENSE,
            accountId = "acc-1",
        )
        val details = TransactionWithDetails(transaction = tx)

        val catItem = Category(
            id = "cat-1",
            name = "Belanja",
            type = TypeCategory.EXPENSE,
            iconId = "shopping_cart",
            color = "#4CAF50",
        ).toTransactionCategoryItemModel()

        val allCategories = listOf(
            com.example.nexcashapplication.feature.transaction.presentation.addnew.model.TransactionCategoryAndSubModel(
                categoriItem = catItem,
                subCategoriesItem = emptyList(),
            )
        )
        val availableAccounts = listOf(
            AccountUiModel(
                id = "acc-1",
                name = "Dompet",
                provider = null,
                balance = 500_000L,
            )
        )

        val state = details.toAddEditTransactionState(
            allCategories = allCategories,
            availableAccounts = availableAccounts,
        )

        assertEquals("tx-100", state.transactionId)
        assertEquals(TypeCategory.EXPENSE, state.type)
        assertEquals(85_000L, state.amount)
        assertEquals("Belanja", state.nameTransaction)
        assertEquals("cat-1", state.selectedCategoryId)
        assertEquals("acc-1", state.selectedAccountId)
        assertEquals(instant, state.transactionAt)
        assertEquals("Catatan belanja", state.note)
        assertEquals(allCategories, state.allCategories)
        assertEquals(availableAccounts, state.availableAccounts)
    }

    @Test
    fun `toAddEditTransactionState saat description null note bernilai string kosong`() {
        val instant = Instant.fromEpochMilliseconds(1_700_000_000_000L)
        val tx = Transaction(
            id = "tx-200",
            amount = 10_000L,
            title = "Parkir",
            categoryId = "cat-2",
            description = null,
            dateTime = instant,
            typeTransaction = TypeCategory.EXPENSE,
            accountId = "acc-2",
        )
        val details = TransactionWithDetails(transaction = tx)

        val state = details.toAddEditTransactionState(
            allCategories = emptyList(),
            availableAccounts = emptyList(),
        )

        assertEquals("", state.note)
    }
}
