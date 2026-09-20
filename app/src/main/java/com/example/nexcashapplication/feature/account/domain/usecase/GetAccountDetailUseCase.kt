package com.example.nexcashapplication.feature.account.domain.usecase

import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.account.domain.model.AccountModel
import com.example.nexcashapplication.feature.account.domain.repository.AccountRepository
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetAccountDetailUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(accountId: String): Flow<AccountDetailData> {
        val asset = accountRepository.getAccountById(accountId)
            ?: throw NoSuchElementException("Asset not found")

        val recentTransactions = transactionRepository.getRecentTransactionsByCategory(
            6, ""
        )

        return combine(
            asset,
            recentTransactions
        ){ account, recentTransactions ->
            AccountDetailData(account, recentTransactions)
        }
    }
}

data class AccountDetailData(
    val account: AccountModel,
    val recentTransactions: List<TransactionWithDetails>,
)