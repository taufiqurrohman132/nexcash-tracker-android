package com.example.nexcashapplication.feature.transaction.presentation.main.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import com.example.nexcashapplication.core.domain.model.TransactionItemUiModel
import com.example.nexcashapplication.feature.transaction.domain.model.TransactionWithDetails
import com.example.nexcashapplication.feature.transaction.presentation.main.model.TransactionHeaderUiModel
import com.example.nexcashapplication.feature.transaction.presentation.main.model.TransactionUiGroupModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

//
//private val timeFormatter = DateTimeFormatter.ofPattern("dd MMM, HH:mm")
//        .withZone(ZoneId.systemDefault())

fun TransactionWithDetails.toItemUiModel() = TransactionItemUiModel(
    id = transaction.id,
    title = transaction.title,
    time = transaction.dateTime.toEpochMilliseconds().toString(),
    amount = transaction.amount,
    isIncome = transaction.isIncome,
    category = category?.name ?: "Tanpa Kategori",
    account = account?.name ?: "Akun Dihapus",
    icon = Icons.Default.Category,
)

fun List<TransactionWithDetails>.toGroupHeaderUiModel(localDate: LocalDate): TransactionHeaderUiModel {
    val totalAmount = sumOf { transactionWithDetails ->
        if (transactionWithDetails.transaction.isIncome)
            transactionWithDetails.transaction.amount
        else -transactionWithDetails.transaction.amount
    }

    return TransactionHeaderUiModel(
        id = localDate.toString(),               // identitas grup = tanggal, bukan id transaksi manapun
        title = localDate.toString(),       // "Hari ini" / "Kemarin" / "24 Mei 2026"
        time = localDate
                .atStartOfDayIn(TimeZone.currentSystemDefault())
                .toEpochMilliseconds(),               // kalau field `time` di header memang perlu Long
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
