package com.example.nexcashapplication.core.domain.model

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

    companion object {
        fun fromCode(code: String): AppLanguage = entries.find { it.code == code } ?: INDONESIA
    }
}