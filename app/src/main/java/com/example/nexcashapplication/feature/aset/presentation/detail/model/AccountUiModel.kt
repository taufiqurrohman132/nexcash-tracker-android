package com.example.nexcashapplication.feature.aset.presentation.detail.model

import androidx.compose.runtime.Immutable


@Immutable
data class AccountUiModel(
    val id: String,
    val name: String,
    val provider: String,
    val maskedNumber: String,
    val balance: Long,
    val iconUrl: String? = null,
)