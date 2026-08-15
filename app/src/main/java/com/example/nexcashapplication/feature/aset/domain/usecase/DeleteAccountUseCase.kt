package com.example.nexcashapplication.feature.aset.domain.usecase

import com.example.nexcashapplication.feature.aset.domain.repository.AccountRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(accountId: String) {
        accountRepository.deleteAccount(accountId)
    }
}