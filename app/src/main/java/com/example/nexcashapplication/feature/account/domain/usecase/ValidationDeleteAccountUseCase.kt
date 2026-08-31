package com.example.nexcashapplication.feature.account.domain.usecase

import com.example.nexcashapplication.core.domain.repository.TransactionRepository
import com.example.nexcashapplication.feature.account.domain.model.DeleteAccountDesision
import com.example.nexcashapplication.feature.account.domain.repository.AccountRepository
import com.example.nexcashapplication.feature.account.presentation.shared.resolveDeleteState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ValidationDeleteAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(accountId: String): Flow<DeleteAccountDesision> {
        return combine(
            accountRepository.getAccountById(accountId),
            transactionRepository.getCountTransactionsByAccount(accountId),
        ) { account, transactionCount ->
            account.resolveDeleteState(transactionCount)
        }
    }
}