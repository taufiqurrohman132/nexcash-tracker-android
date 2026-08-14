package com.example.nexcashapplication.feature.aset.domain.model

sealed interface DeleteAccountDesision {
    data class Blocked(val balance: Long) : DeleteAccountDesision
    data class ConfirmWithHistory(val transactionCount: Int) : DeleteAccountDesision
    data object ConfirmSimple : DeleteAccountDesision
}