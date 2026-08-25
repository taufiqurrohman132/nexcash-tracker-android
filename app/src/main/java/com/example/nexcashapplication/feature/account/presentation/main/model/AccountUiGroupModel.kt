package com.example.nexcashapplication.feature.account.presentation.main.model

import androidx.compose.runtime.Immutable


@Immutable
data class AccountUiGroupModel(
    val nameGroup: String,
    val listAccount: List<AccountUiModel> = emptyList()
)