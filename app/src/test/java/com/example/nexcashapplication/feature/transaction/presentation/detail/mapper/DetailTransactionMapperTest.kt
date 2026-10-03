package com.example.nexcashapplication.feature.transaction.presentation.detail.mapper

import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.extension.formatCurrency
import com.example.nexcashapplication.feature.account.domain.model.AccountModel
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.ZoneId
import kotlin.time.Instant
import kotlin.time.toKotlinInstant

/**
 * Unit test mapper layar detail transaksi:
 * - [toDetailTransactionUiModel]: field transaksi dipindah apa adanya, kategori &
 *   akun diubah ke model UI; lampiran masih `null` (belum dipetakan dari domain).
 * - [toDeleteDialogUiState]: teks dialog hapus — pengeluaran dihapus membuat saldo
 *   **bertambah**, pemasukan **berkurang**; nama akun kosong bila akunnya hilang.
 */
class DetailTransactionMapperTest {

    @Test
    fun `memetakan transaksi lengkap dengan kategori dan akun ke model UI`() {
        val ui = sample().toDetailTransactionUiModel()

        assertEquals("t1", ui.transactionId)
        assertEquals("Kopi pagi", ui.name)
        assertEquals(150_000, ui.amount)
        assertEquals(TransactionType.EXPENSE, ui.type)
        assertEquals(at, ui.dateTime)
        assertEquals("Rapat tim", ui.note)
        assertEquals("Makanan", ui.category?.name)
        assertEquals("BCA", ui.account?.name)
        assertNull(ui.attachment, "lampiran belum dipetakan dari domain")
    }

    @Test
    fun `tanpa kategori dan akun model UI tetap terbentuk`() {
        val ui = sample(category = null, account = null, note = null).toDetailTransactionUiModel()

        assertNull(ui.category)
        assertNull(ui.account)
        assertNull(ui.note)
        assertEquals("Kopi pagi", ui.name)
    }

    @Test
    fun `dialog hapus - pengeluaran membuat saldo bertambah`() {
        val dialog = sample().toDetailTransactionUiModel().toDeleteDialogUiState()

        assertEquals("Kopi pagi", dialog.title)
        assertEquals("EXPENSE", dialog.typeLabel)
        assertEquals("BCA", dialog.accountName)
        assertEquals(150_000L.formatCurrency(), dialog.formattedAmount)
        assertTrue(
            dialog.balanceEffectText.contains("bertambah"),
            "saldo harus bertambah setelah pengeluaran dihapus: ${dialog.balanceEffectText}",
        )
        assertTrue(dialog.balanceEffectText.contains("BCA"), dialog.balanceEffectText)
    }

    @Test
    fun `dialog hapus - pemasukan membuat saldo berkurang dan nama akun kosong`() {
        val dialog = sample(type = TransactionType.INCOME, account = null)
                .toDetailTransactionUiModel()
                .toDeleteDialogUiState()

        assertEquals("INCOME", dialog.typeLabel)
        assertEquals("", dialog.accountName)
        assertTrue(
            dialog.balanceEffectText.contains("berkurang"),
            "saldo harus berkurang setelah pemasukan dihapus: ${dialog.balanceEffectText}",
        )
    }

    // ── Peralatan ──────────────────────────────────────────────────────

    private val at: Instant = instant(2026, 10, 1, 8, 30)

    private val kategori = Category(
        id = "food",
        parentId = null,
        name = "Makanan",
        type = TypeCategory.EXPENSE,
        iconId = "coffee",
        color = "#FF0000",
    )

    private val akun = AccountModel(
        id = "bca",
        accountType = TypeAccount.BANK,
        name = "BCA",
    )

    private fun sample(
        type: TransactionType = TransactionType.EXPENSE,
        category: Category? = kategori,
        account: AccountModel? = akun,
        note: String? = "Rapat tim",
    ) = TransactionWithDetails(
        transaction = Transaction(
            id = "t1",
            title = "Kopi pagi",
            amount = 150_000,
            categoryId = category?.id,
            description = note,
            dateTime = at,
            typeTransaction = type,
            accountId = account?.id,
        ),
        category = category,
        account = account,
    )

    /**
     * Instant pada jam lokal tertentu — `dateTime` dipindah mentah oleh mapper,
     * tapi fixture tetap dibuat dari zona sistem supaya deterministik.
     */
    private fun instant(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
    ): Instant =
        java.time.ZonedDateTime.of(year, month, day, hour, minute, 0, 0, ZoneId.systemDefault())
                .toInstant()
                .toKotlinInstant()
}
