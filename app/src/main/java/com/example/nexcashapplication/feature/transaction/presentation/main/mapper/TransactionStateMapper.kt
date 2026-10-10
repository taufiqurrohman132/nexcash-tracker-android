package com.example.nexcashapplication.feature.transaction.presentation.main.mapper

import androidx.compose.ui.graphics.Color
import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel
import com.example.nexcashapplication.feature.category.presentation.addnew.constant.CategoryIcons
import com.example.nexcashapplication.feature.common.extentions.toColor
import com.example.nexcashapplication.feature.common.utils.formatDate
import com.example.nexcashapplication.feature.common.utils.formatTime
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import com.example.nexcashapplication.core.domain.model.TypeCategory
import com.example.nexcashapplication.feature.transaction.presentation.main.model.TransactionHeaderUiModel
import com.example.nexcashapplication.feature.transaction.presentation.main.model.TransactionUiGroupModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

//
//private val timeFormatter = DateTimeFormatter.ofPattern("dd MMM, HH:mm")
//        .withZone(ZoneId.systemDefault())


fun TransactionWithDetails.toItemUiModel() = TransactionItemUiModel(
    id = transaction.id,
    name = transaction.title,
    time = transaction.dateTime.formatTime(),
    amount = transaction.amount,
    typeTransaction = transaction.typeTransaction,
    // null saat kategori/akun sudah dihapus — teks cadangan dipilih di layer UI
    // (lihat TransactionItem) supaya bisa ikut bahasa aplikasi.
    category = category?.name,
    account = account?.name,
    icon = CategoryIcons.fromId(category?.iconId ?: "pet").icon,
    iconColor = category?.color?.toColor() ?: Color.Black,
)

fun List<TransactionWithDetails>.toGroupHeaderUiModel(localDate: LocalDate): TransactionHeaderUiModel {
    val totalAmount = sumOf { transactionWithDetails ->
        if (transactionWithDetails.transaction.typeTransaction == TypeCategory.INCOME)
            transactionWithDetails.transaction.amount
        else -transactionWithDetails.transaction.amount
    }
    val latestDateTime = maxOf { it.transaction.dateTime } // Instant, punya jam asli

    return TransactionHeaderUiModel(
        id = localDate.toString(),               // identitas grup = tanggal, bukan id transaksi manapun
        title = localDate.formatDate(),       // "Hari ini" / "Kemarin" / "24 Mei 2026"
        time = latestDateTime.formatTime(),               // kalau field `time` di header memang perlu Long
        totalAmount = totalAmount,
        isExpanded = false,
    )
}

fun List<TransactionWithDetails>.toItemUiModelList(): List<TransactionUiGroupModel> = groupBy {
    it.transaction.dateTime.toLocalDateTime(TimeZone.currentSystemDefault()).date
}.map { (localDate, transactions) ->
    TransactionUiGroupModel(
        header = transactions.toGroupHeaderUiModel(localDate),
        listTransaction = transactions.map {
            it.toItemUiModel()
        },
    )
}


