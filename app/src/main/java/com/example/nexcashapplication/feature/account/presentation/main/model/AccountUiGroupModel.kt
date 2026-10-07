package com.example.nexcashapplication.feature.account.presentation.main.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable


@Immutable
data class AccountUiGroupModel(
    /** Judul grup berupa string resource — resolve di UI memakai `stringResource`. */
    @StringRes
    val nameGroupRes: Int,
    val listAccount: List<AccountUiModel> = emptyList()
)