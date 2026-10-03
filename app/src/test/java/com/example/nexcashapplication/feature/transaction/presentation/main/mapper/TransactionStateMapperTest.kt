package com.example.nexcashapplication.feature.transaction.presentation.main.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Pets
import com.example.nexcashapplication.core.domain.model.Category
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.account.domain.model.AccountModel
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount
import com.example.nexcashapplication.feature.transaction.domain.model.Transaction
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.time.ZoneId
import kotlin.time.Instant
import kotlin.time.toKotlinInstant

/**
 * Unit test mapper daftar transaksi tab Transaksi utama:
 * - [toItemUiModel]: pemetaan field per baris + label cadangan saat kategori/akun
 *   hilang, ikon ngikut `iconId` kategori.
 * - [toItemUiModelList]: pengelompokan per tanggal **mengikuti urutan data query**
 *   (mapper tidak mengurutkan ulang — yang urut datang dari Room).
 * - [toGroupHeaderUiModel]: total grup = pemasukan − pengeluaran, jam = data
 *   transaksi terakhir, id grup = tanggalnya.
 *
 * Warna sengaja tidak diuji: `String.toColor()` melewati `android.graphics.Color`
 * yang di JVM unit test selalu membalas 0 (mockable jar).
 */
class TransactionStateMapperTest {

    @Test
    fun `toItemUiModel - memetakan field inti dan ikon dari iconId kategori`() {
        val item = sample(at = instant(2026, 10, 1, 8, 10)).toItemUiModel()

        assertEquals("t1", item.id)
        assertEquals("Kopi pagi", item.name)
        assertEquals("08:10", item.time)
        assertEquals(150_000, item.amount)
        assertEquals(TransactionType.EXPENSE, item.typeTransaction)
        assertEquals("Makanan", item.category)
        assertEquals("BCA", item.account)
        assertEquals(Icons.Default.Coffee, item.icon)
    }

    @Test
    fun `toItemUiModel - tanpa kategori dan tanpa akun memakai label cadangan`() {
        val item = sample(category = null, account = null).toItemUiModel()

        assertEquals("Tanpa Kategori", item.category)
        assertEquals("Akun Dihapus", item.account)
        assertEquals(Icons.Default.Pets, item.icon, "iconId default kategori hilang = pet")
    }

    @Test
    fun `toItemUiModelList - dikelompokkan per tanggal sesuai urutan data query`() {
        val rows = listOf(
            sample(id = "pagi", at = instant(2026, 10, 1, 8, 10)),
            sample(id = "sore", at = instant(2026, 10, 1, 17, 40)),
            sample(id = "kemarin", at = instant(2026, 9, 30, 9, 0)),
        )

        val groups = rows.toItemUiModelList()

        assertEquals(listOf("2026-10-01", "2026-09-30"), groups.map { it.header.id })
        assertEquals(listOf("pagi", "sore"), groups[0].listTransaction.map { it.id })
        assertEquals(listOf("kemarin"), groups[1].listTransaction.map { it.id })
    }

    @Test
    fun `toItemUiModelList - daftar kosong menghasilkan grup kosong`() {
        assertEquals(emptyList<Any>(), emptyList<TransactionWithDetails>().toItemUiModelList())
    }

    @Test
    fun `toGroupHeaderUiModel - total adalah pemasukan dikurangi pengeluaran`() {
        val rows = listOf(
            sample(id = "gaji", type = TransactionType.INCOME, amount = 200_000),
            sample(id = "kopi", type = TransactionType.EXPENSE, amount = 50_000),
            sample(id = "taksi", type = TransactionType.EXPENSE, amount = 15_000),
        )

        val header = rows.toGroupHeaderUiModel(LocalDate(2026, 10, 1))

        assertEquals(200_000 - 50_000 - 15_000, header.totalAmount)
    }

    @Test
    fun `toGroupHeaderUiModel - id grup tanggal, judulnya tahun itu, jam data terakhir`() {
        val rows = listOf(
            sample(id = "awal", at = instant(2026, 10, 1, 7, 15)),
            sample(id = "akhir", at = instant(2026, 10, 1, 21, 45)),
        )

        val header = rows.toGroupHeaderUiModel(LocalDate(2026, 10, 1))

        assertEquals("2026-10-01", header.id)
        assertEquals("21:45", header.time, "jam header = transaksi terakhir, bukan yang pertama")
        assertEquals(false, header.isExpanded, "grup selalu mulai terlipat")
        assertFalse(header.title.isBlank())
        assertEquals(true, header.title.endsWith("2026"), "judul memuat tahun: ${header.title}")
    }

    // ── Peralatan ──────────────────────────────────────────────────────

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
        id: String = "t1",
        amount: Long = 150_000,
        type: TransactionType = TransactionType.EXPENSE,
        at: Instant = instant(2026, 10, 1, 8, 30),
        category: Category? = kategori,
        account: AccountModel? = akun,
    ) = TransactionWithDetails(
        transaction = Transaction(
            id = id,
            title = "Kopi pagi",
            amount = amount,
            categoryId = category?.id,
            description = null,
            dateTime = at,
            typeTransaction = type,
            accountId = account?.id,
        ),
        category = category,
        account = account,
    )

    /**
     * Instant pada jam lokal tertentu — supaya `formatTime()` dan pengelompokan
     * tanggal menghasilkan hasil yang sama berapa pun zona sistem test.
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
