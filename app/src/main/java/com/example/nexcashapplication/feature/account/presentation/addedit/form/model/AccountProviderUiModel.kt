package com.example.nexcashapplication.feature.account.presentation.addedit.form.model

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.feature.account.domain.model.TypeAccount

data class AccountProviderUiModel(
    val id: String,
    val name: String,
    val typeAccount: TypeAccount,
    val color: Color
    // iconRes: Int  // ganti dengan resource icon provider sesungguhnya
)
