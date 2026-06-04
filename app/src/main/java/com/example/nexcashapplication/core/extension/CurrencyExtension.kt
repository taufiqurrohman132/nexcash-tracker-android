package com.example.nexcashapplication.core.extension

import java.text.NumberFormat
import java.util.Locale

fun Long.formatCurrency(): String {
    return NumberFormat
        .getNumberInstance(Locale("id", "ID"))
        .format(this)
}