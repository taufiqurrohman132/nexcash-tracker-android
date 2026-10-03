package com.example.nexcashapplication.feature.budgets.domain.usecase

import com.example.nexcashapplication.core.domain.model.Budget
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.core.domain.repository.BudgetRepository
import com.example.nexcashapplication.core.domain.repository.CategoryRepository
import com.example.nexcashapplication.feature.budgets.domain.model.RecurrenceType
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

/**
 * Logic bisnis penyimpanan anggaran (create / update instance / ganti tipe periode).
 *
 * Aturan periode:
 * - MONTHLY : window = kalender 1 .. akhir bulan (anchor [Request.today]), SELALU berulang.
 * - WEEKLY  : window = minggu locale yang mencakup [Request.today], SELALU berulang.
 * - CUSTOM  : window ditentukan user; berulang sesuai switch (one-shot bila mati).
 *
 * Alur simpan:
 * - Tidak ada instance aktif / tipe berubah → tutup seri lama (hapus baris mulai >= windowStart,
 *   potong baris yang membentang melewati windowStart, matikan is_recurring root) lalu insert
 *   root baru (mapper memberi recurring_group_id = id sendiri).
 * - Tipe sama → update HANYA instance yang sedang berjalan + baris seri di periode mendatang;
 *   riwayat tidak berubah. Baris yang belum di-generate akan meng-copy amount terakhir.
 *
 * Aturan tipe kategori:
 * - Anggaran = batas belanja, jadi hanya berlaku untuk kategori **pengeluaran**.
 *   Kategori pemasukan ditolak: "terpakai/sisa" tidak bermakna untuk pemasukan, dan
 *   Statistik menjumlahkan seluruh baris budget sebagai total batas pengeluaran.
 */
class SaveBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository,
    private val getBudgetPeriodUseCase: GetBudgetPeriodUseCase,
) {

    sealed interface Result {
        data object Success : Result
        data class Failure(val message: String) : Result
    }

    data class Request(
        val budgetId: String? = null,
        val categoryId: String?,
        val amount: Long,
        val periodType: RecurrenceType,
        val customStart: LocalDate,
        val customEnd: LocalDate,
        val isRecurringRequested: Boolean,
        val today: LocalDate = LocalDate.now(),
    )

    suspend operator fun invoke(request: Request): Result {
        val catId = request.categoryId
            ?: return Result.Failure("Kategori tidak ditemukan")

        // Anggaran hanya untuk kategori pengeluaran. Kategori yang tidak ditemukan
        // diteruskan ke validasi berikutnya seperti biasa — yang ditolak hanya pemasukan.
        val category = categoryRepository.getCategoryById(catId).first()
        if (category?.type == TypeCategory.INCOME) {
            return Result.Failure("Anggaran hanya bisa diset untuk kategori pengeluaran")
        }

        if (request.amount <= 0L) {
            return Result.Failure("Batas anggaran harus lebih dari 0")
        }
        if (request.periodType == RecurrenceType.CUSTOM &&
            !request.customEnd.isAfter(request.customStart)
        ) {
            return Result.Failure("Tanggal berakhir harus setelah tanggal mulai")
        }

        val active = request.budgetId?.let { budgetRepository.getById(it) }
            ?: budgetRepository.getActiveBudgetForCategory(catId, request.today).first()

        // MONTHLY/WEEKLY selalu berulang; hanya CUSTOM yang bisa one-shot.
        val isRecurring =
            if (request.periodType == RecurrenceType.CUSTOM) request.isRecurringRequested else true

        // Buat baru / ganti tipe → window dari periode "today"; tipe sama → pakai window tersimpan.
        val isSameType = active != null && active.recurrenceType == request.periodType
        val (windowStart, windowEnd) = when {
            request.periodType == RecurrenceType.CUSTOM ->
                request.customStart to request.customEnd

            active != null && isSameType ->
                active.startDate to active.endDate // jangan ubah window tersimpan

            else -> getBudgetPeriodUseCase(
                isRecurring = isRecurring,
                type = request.periodType,
                startDate = request.today,
                endDate = null,
            )
        }

        return try {
            if (active == null || !isSameType) {
                // Buat baru / ganti tipe: tutup dulu baris seri lama di kategori ini.
                closeSeries(
                    categoryId = catId,
                    windowStart = windowStart,
                )
                budgetRepository.insertBudget(
                    Budget(
                        categoryId = catId,
                        startDate = windowStart,
                        endDate = windowEnd,
                        amount = request.amount,
                        recurrenceType = request.periodType,
                        isRecurring = isRecurring,
                    ),
                )
            } else {
                val rootId = active.recurringGroupId ?: active.id

                // Tipe sama: instance aktif + periode mendatang dalam seri yang sama mewarisi
                // amount baru; riwayat (periode sebelumnya) tidak disentuh.
                val seriesRows = budgetRepository.getByCategory(catId).first()
                    .filter { row ->
                        val rowGroupId = row.recurringGroupId ?: row.id
                        row.id != active.id && rowGroupId == rootId &&
                                !row.startDate.isBefore(active.startDate)
                    }
                seriesRows.forEach { row ->
                    budgetRepository.updateBudget(row.copy(amount = request.amount))
                }

                budgetRepository.updateBudget(
                    active.copy(
                        amount = request.amount,
                        isRecurring = isRecurring,
                        startDate = windowStart,
                        endDate = windowEnd,
                    ),
                )

                // Sinkronkan flag seri dengan switch user (root ≠ instance aktif).
                if (rootId != null && rootId != active.id) {
                    budgetRepository.getById(rootId)
                        ?.takeIf { it.isRecurring != isRecurring }
                        ?.let { budgetRepository.updateBudget(it.copy(isRecurring = isRecurring)) }
                }
            }
            Result.Success
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure("Gagal menyimpan anggaran: ${e.message}")
        }
    }

    /**
     * Tutup seri lama pada [categoryId] supaya tidak tumpang tindih dengan window baru:
     * - start >= windowStart        → dihapus (anak duluan: urut start desc, root paling akhir
     *                                 biar FK RESTRICT lolos)
     * - start < windowStart s/d end >= windowStart → end dipotong ke windowStart-1,
     *                                 is_recurring = false
     * - baris masa lalu (end < windowStart) → tidak disentuh (riwayat tetap)
     * Semua root seri yang tersentuh dimatikan generasinya.
     */
    private suspend fun closeSeries(
        categoryId: String,
        windowStart: LocalDate,
    ) {
        val rows = budgetRepository.getByCategory(categoryId).first()

        val toDelete = rows.filter { !it.startDate.isBefore(windowStart) }
            .sortedByDescending { it.startDate }
        val toTruncate = rows.filter {
            it.startDate.isBefore(windowStart) && !it.endDate.isBefore(windowStart)
        }

        val touchedRoots = (toDelete + toTruncate)
            .mapNotNull { it.recurringGroupId ?: it.id }
            .toSet()

        toDelete.forEach { budgetRepository.deleteBudget(it) }
        toTruncate.forEach {
            budgetRepository.updateBudget(
                it.copy(endDate = windowStart.minusDays(1), isRecurring = false),
            )
        }

        touchedRoots.forEach { rootId ->
            budgetRepository.getById(rootId)
                ?.takeIf { it.isRecurring }
                ?.let { budgetRepository.updateBudget(it.copy(isRecurring = false)) }
        }
    }
}
