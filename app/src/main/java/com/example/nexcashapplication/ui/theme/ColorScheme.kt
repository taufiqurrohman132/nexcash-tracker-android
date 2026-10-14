package com.example.nexcashapplication.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
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
// Generator scheme
//
// Warna yang dipilih user adalah *seed*: dari situ dihitung primary, onPrimary,
// primaryContainer dan onPrimaryContainer, supaya tombol/chip ikut kontras.
//
// Tanpa perhitungan ini, memilih Hitam menghasilkan primary Hitam dengan teks putih
// di atasnya — tidak terbaca. Kontrasnya dihitung dari luminance seed.
// ============================================================================

/** Teks yang terbaca di atas warna ini: hitam untuk seed terang, putih untuk gelap. */
private fun Color.contrastText(): Color = if (luminance() > 0.5f) Color.Black else Color.White

/** Campur warna ini ke [target] sejauh [fraction] (0 = tetap, 1 = berubah penuh). */
private fun Color.mix(target: Color, fraction: Float): Color = Color(
    red = red + (target.red - red) * fraction,
    green = green + (target.green - green) * fraction,
    blue = blue + (target.blue - blue) * fraction,
)

/** Secondary yang diredam dari seed — supaya tidak identik dengan primary. */
private fun Color.muted(): Color = mix(TextSecondary, 0.45f)

/**
 * Scheme untuk tema terang. Netral (background/surface/onBackground/onSurface) sengaja
 * TIDAK diambil dari seed: teks harus tetap terbaca berapa pun warna yang dipilih.
 */
fun lightColorSchemeFrom(seed: Color): ColorScheme {
    val container = seed.mix(Color.White, 0.85f)
    return lightColorScheme(
        primary = seed,
        onPrimary = seed.contrastText(),
        primaryContainer = container,
        onPrimaryContainer = container.contrastText(),

        secondary = seed.muted(),
        onSecondary = seed.muted().contrastText(),

        tertiary = seed,
        onTertiary = seed.contrastText(),

        background = BackgroundLight,
        onBackground = TextPrimary,
        surface = SurfaceWhite,
        onSurface = TextPrimary,
        onSurfaceVariant = TextSecondary,
        outline = BorderGray,
    )
}

/**
 * Scheme untuk tema gelap. Container diredam ke arah hitam supaya tidak menyilaukan,
 * dan netralnya digelapkan secara terpisah dari seed.
 */
fun darkColorSchemeFrom(seed: Color): ColorScheme {
    val container = seed.mix(Color.Black, 0.6f)
    return darkColorScheme(
        primary = seed,
        onPrimary = seed.contrastText(),
        primaryContainer = container,
        onPrimaryContainer = container.contrastText(),

        secondary = seed.muted(),
        onSecondary = seed.muted().contrastText(),

        tertiary = seed,
        onTertiary = seed.contrastText(),

        background = DarkBackground,
        onBackground = DarkOnBackground,
        surface = DarkBackground,
        onSurface = DarkOnBackground,
        onSurfaceVariant = TextTertiary,
        outline = DarkOutline,
    )
}

// Netral tema gelap — netral terang (`BackgroundLight`/`SurfaceWhite`) akan membuat
// halaman putih menyilaukan di atas latar gelap.
private val DarkBackground = Color(0xFF111827)
private val DarkOnBackground = Color(0xFFF9FAFB)
private val DarkOutline = Color(0xFF374151)
