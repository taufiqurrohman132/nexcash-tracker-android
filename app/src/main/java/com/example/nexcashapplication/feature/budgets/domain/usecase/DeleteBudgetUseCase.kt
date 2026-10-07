package com.example.nexcashapplication.feature.budgets.domain.usecase

import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.repository.BudgetRepository
import com.example.nexcashapplication.core.i18n.UiText
import com.example.nexcashapplication.core.i18n.uiText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

/**
 * Hapus anggaran = **tutup seri**, bukan hapus semua riwayat.
 *
 * Aturan (konsisten dengan [SaveBudgetUseCase.closeSeries] & prinsip
 * "riwayat tidak boleh dimutasi"):
 * - Baris dalam seri yang masih relevan (`end >= [today]`) → **dihapus**
 *   (instance aktif + seluruh periode mendatang), urut `start DESC`
 *   supaya anak terhapus sebelum root (FK RESTRICT).
 * - Baris lampau (`end < [today]`) → **dipertahankan** sebagai riwayat,
 *   tapi `is_recurring` dimatikan supaya [EnsureUpToDateBudgetsUseCase]
 *   tidak menghidupkan periode lagi.
 * - Seri/kategori lain tidak tersentuh.
 *
 * Root yang ikut terhapus tidak masalah: tidak ada baris tersisa yang
 * menunjuk ke unique index (recurring_group_id, start_date) jadi tetap aman.
 * Root yang tersimpan (lampau) dimatikan generasinya lewat `is_recurring = false`.
 */
class DeleteBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository,
) {

    sealed interface Result {
        data object Success : Result
        data class Failure(val message: UiText) : Result
    }

    /**
     * @param target baris anggaran yang menjadi titik hapus (instance aktif).
     * @param today  anchor periode; baris dengan `end >= today` dianggap
     *               aktif/mendatang dan ikut dihapus.
     */
    suspend operator fun invoke(
        target: Budget,
        today: LocalDate = LocalDate.now(),
    ): Result {
        val groupId = target.recurringGroupId ?: target.id
            ?: return Result.Failure(uiText(R.string.budget_error_invalid_budget))

        return try {
            val rows = budgetRepository.getByCategory(target.categoryId).first()
                .filter { (it.recurringGroupId ?: it.id) == groupId }

            val (history, toDelete) = rows.partition { it.endDate.isBefore(today) }

            // Anak duluan: start terbesar dihapus lebih dulu (root paling awal → terakhir)
            toDelete.sortedByDescending { it.startDate }
                .forEach { budgetRepository.deleteBudget(it) }

            // Riwayat tetap ada, tapi generasi periode berikutnya dihentikan
            history.filter { it.isRecurring }
                .forEach { budgetRepository.updateBudget(it.copy(isRecurring = false)) }

            Result.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Detail exception teknis (Inggris) tetap ditampilkan sebagai argumen,
            // prefix-nya tetap terikat bahasa aktif lewat string resource.
            Result.Failure(uiText(R.string.budget_delete_failed, e.message ?: e.toString()))
        }
    }
}
