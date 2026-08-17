package com.example.nexcashapplication.feature.aset.presentation.main.mapper

import com.example.nexcashapplication.feature.aset.domain.model.AccountModel
import com.example.nexcashapplication.feature.aset.presentation.addedit.typeoption.mapper.toLabel
import com.example.nexcashapplication.feature.aset.presentation.main.model.AccountUiGroupModel
import com.example.nexcashapplication.feature.aset.presentation.main.model.AccountUiModel

fun AccountModel.toAccountUiModel() = AccountUiModel(
    id = id,
    name = name,
    provider = providerAccount,
    balance = currentBalance,
    iconUrl = "",
)

fun List<AccountModel>.toAccountUiModelList(): List<AccountUiGroupModel> = groupBy {
    it.accountType
}.map { (type, accounts) ->
    AccountUiGroupModel(
        type.toLabel(),
        accounts.sortedByDescending { it.lastUpdateAt }.map { it.toAccountUiModel() },
    )
}

