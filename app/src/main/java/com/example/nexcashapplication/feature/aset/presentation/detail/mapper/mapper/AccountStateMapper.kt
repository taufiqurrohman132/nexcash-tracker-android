package com.example.nexcashapplication.feature.aset.presentation.detail.mapper.mapper

import com.example.nexcashapplication.feature.aset.domain.model.AccountModel
import com.example.nexcashapplication.feature.aset.presentation.main.model.AccountUiModel

fun AccountModel.toAccountUiModel() = AccountUiModel(
    id = id,
    name = name,
    provider = institution ?: "",
    maskedNumber = accountNumber ?: "",
    balance = currentBalance,
    iconUrl = "",
)

fun List<AccountModel>.toAccountUiModelList(): List<AccountUiModel> = map { it.toAccountUiModel() }