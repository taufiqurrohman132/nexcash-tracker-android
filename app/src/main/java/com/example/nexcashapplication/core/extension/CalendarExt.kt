package com.example.nexcashapplication.core.extension

import java.time.Instant
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

fun Long.toDayName(): String{
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .dayOfWeek
        .getDisplayName(TextStyle.FULL, Locale.getDefault())
}