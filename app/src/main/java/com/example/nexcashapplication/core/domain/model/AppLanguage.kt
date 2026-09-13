package com.example.nexcashapplication.core.domain.model

import java.util.Locale

enum class AppLanguage(val code: String) {
    INDONESIA("id"),
    ENGLISH("en"),
    FRENCH("fr"),
    GERMAN("de"),
    SPANISH("es"),
    PORTUGUESE("pt"),
    RUSSIAN("ru"),
    CHINESE("zh"),
    JAPANESE("ja");

    val displayNmae: String
        get() = Locale(code).getDisplayName(Locale(code))
                .replaceFirstChar { it.uppercase() }

    companion object {
        fun fromCode(code: String): AppLanguage = entries.find { it.code == code } ?: INDONESIA
    }
}