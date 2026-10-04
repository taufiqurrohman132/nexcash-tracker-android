package com.example.nexcashapplication.feature.notification.domain.usecase

import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.common.utils.toEndOfDayMillis
import com.example.nexcashapplication.feature.common.utils.toStartOfDayMillis
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

/**
 * true kalau hari ini sudah ada transaksi (apa pun, yang belum dihapus).
 *
 * Dipakai pengingat malam: kalau belum ada yang dicatat sama sekali, pengguna diingatkan.
 * Rentangnya hari kalender (00.00–23.59 di zona perangkat), bukan 24 jam terakhir.
 */
class HasTransactionTodayUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(now: Long = System.currentTimeMillis()): Boolean {
        val today = Instant.ofEpochMilli(now).atZone(ZoneId.systemDefault()).toLocalDate()
        return transactionRepository.countTransactionsInRange(
            start = today.toStartOfDayMillis(),
            end = today.toEndOfDayMillis(),
        ) > 0
    }
}
