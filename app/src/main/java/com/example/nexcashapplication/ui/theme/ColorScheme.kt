package com.example.nexcashapplication.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.util.Locale

// ============================================================================
// Hex ↔ Color
//
// DataStore menyimpan warna sebagai hex `#RRGGBB` (lihat AppSettings.color) — bukan
// `Color` — karena `Color` adalah tipe Compose yang tidak layak menyeberangi lapisan
// domain/data. Konversinya terpusat di sini supaya tidak ada layar yang menulis
// formatnya sendiri-sendiri.
// ============================================================================

/** Warna bawaan aplikasi — dipakai ketika pengguna belum pernah memilih warna. */
private val DefaultAccent = PrimaryCyan

/** `Color` → `#RRGGBB`. Semua warna di palet app ini opaque, jadi alpha aman dibuang. */
fun Color.toHex(): String =
    String.format(Locale.US, "#%06X", toArgb() and 0xFFFFFF)

/**
 * Hex → [Color]. String kosong (belum pernah memilih) atau tidak terbaca
 * (file DataStore versi lama / korup) → jatuh ke warna bawaan, bukan crash.
 */
fun String.toComposeColor(): Color {
    val hex = removePrefix("#")
    if (hex.length != 6 && hex.length != 8) return DefaultAccent
    val value = hex.toLongOrNull(16) ?: return DefaultAccent
    val argb = if (hex.length == 6) (0xFF000000L or value) else value
    return Color(argb)
}

// ============================================================================
// Color scheme
//
// Nilai warna ada di Color.kt (md_theme_*). Netral dan semantik sengaja tidak
// diturunkan dari warna pilihan user supaya teks tetap terbaca.
// ============================================================================

val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)

val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

// Netral tema gelap — netral terang (`BackgroundLight`/`SurfaceWhite`) akan membuat
// halaman putih menyilaukan di atas latar gelap.
private val DarkBackground = Color(0xFF111827)
private val DarkOnBackground = Color(0xFFF9FAFB)
private val DarkOutline = Color(0xFF374151)
