package com.example.nexcashapplication.feature.aset.presentation.main.model

import androidx.compose.runtime.Immutable


@Immutable
data class AccountUiGroupModel(
    val nameGroup: String,
    val listAccount: List<AccountUiModel> = emptyList()
)