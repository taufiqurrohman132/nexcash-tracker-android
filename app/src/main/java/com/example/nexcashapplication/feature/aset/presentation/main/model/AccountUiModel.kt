package com.example.nexcashapplication.feature.aset.presentation.main.model

import androidx.compose.runtime.Immutable


@Immutable
data class AccountUiModel(
    val id: String,
    val name: String,
    val provider: String,
    val balance: Long,
    val iconUrl: String? = null,
)