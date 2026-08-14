package com.example.nexcashapplication.feature.aset.presentation.addedit.form.model

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.feature.aset.domain.model.TypeAccount

data class AccountProviderUiModel(
    val id: String,
    val name: String,
    val typeAccount: TypeAccount,
    val color: Color
    // iconRes: Int  // ganti dengan resource icon provider sesungguhnya
)
