package com.example.nexcashapplication.core.domain.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.nexcashapplication.feature.transaction.presentation.addnew.TransactionType

/**
 * Baris daftar transaksi.
 *
 * [category] dan [account] **nullable**: kalau kategori/akunnya sudah tidak ada (dihapus),
 * nilainya `null` dan teks cadangan ("Tanpa Kategori" / "Akun Dihapus") dipilih di layer UI
 * oleh [com.example.nexcashapplication.core.component.TransactionItem] lewat string resource.
 * Dulu field-nya String berisi sentinel berbahasa Indonesia, yang mustahil diterjemahkan
 * karena lahir di mapper (tanpa Context).
 */
data class TransactionItemUiModel(
    val id: String?,
    val name: String,
    val time: String,       // "10:45"
    val amount: Long,
    val typeTransaction: TransactionType,
    val category: String?,  // null = kategori sudah dihapus
    val account: String?,   // null = akun sudah dihapus
    val icon: ImageVector, // icon kategori
    val iconColor: Color,
)
