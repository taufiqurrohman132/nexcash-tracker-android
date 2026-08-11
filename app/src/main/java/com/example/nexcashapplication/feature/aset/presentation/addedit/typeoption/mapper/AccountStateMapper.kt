package com.example.nexcashapplication.feature.aset.presentation.addedit.typeoption.mapper

import com.example.nexcashapplication.feature.aset.domain.model.AccountModel
import com.example.nexcashapplication.feature.aset.presentation.main.model.AccountUiModel

fun AccountModel.toAccountUiModel() = AccountUiModel(
    id = id,
    name = name,
    provider = institution?.name ?: "",
    maskedNumber = accountNumber ?: "",
    balance = currentBalance,
    iconUrl = "",
)

fun List<AccountModel>.toAccountUiModelList(): List<AccountUiModel> = map { it.toAccountUiModel() }