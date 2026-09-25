package com.example.nexcashapplication.feature.account.domain.usecase

import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.account.domain.model.AccountModel
import com.example.nexcashapplication.feature.account.domain.repository.AccountRepository
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAccountDetailUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(accountId: String): Flow<AccountDetailData> {
        val asset = accountRepository.getAccountById(accountId)
            ?: throw NoSuchElementException("Asset not found")

        // Dulu: getRecentTransactionsByCategory(6, "") → WHERE category_id = ''
        // tidak pernah cocok (dan itu filter kategori, bukan akun) → daftar selalu kosong.
        // Sekarang: transaksi milik akun ini (urut terbaru), maks RECENT_LIMIT.
        val recentTransactions = transactionRepository
            .getTransactionsByAccount(accountId)
            .map { list -> list.take(RECENT_LIMIT) }

        return combine(
            asset,
            recentTransactions
        ){ account, recentTransactions ->
            AccountDetailData(account, recentTransactions)
        }
    }

    companion object {
        private const val RECENT_LIMIT = 6
    }
}

data class AccountDetailData(
    val account: AccountModel,
    val recentTransactions: List<TransactionWithDetails>,
)
