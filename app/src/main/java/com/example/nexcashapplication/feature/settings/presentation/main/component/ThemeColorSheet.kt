package com.example.nexcashapplication.feature.settings.presentation.main.component

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.ModeNight
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.component.FormField
import com.example.nexcashapplication.core.component.IconCircular
import com.example.nexcashapplication.feature.settings.domain.model.AppThemeMode
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import com.example.nexcashapplication.ui.theme.TextTertiary

/**
 * Bottom sheet pilih tema & warna aksen.
 *
 * Keduanya memanggil callback **tanpa menutup sheet** — pengguna bebas membandingkan
 * beberapa warna, dan penutupan sepenuhnya milik layar pemanggil lewat
 * `SettingsEvent.OnThemeSheetDismiss`.
 */

private class ThemeOption(
    val mode: AppThemeMode,
    val icon: ImageVector,
    @StringRes val labelRes: Int,
)

private val ThemeOptions = listOf(
    ThemeOption(AppThemeMode.LIGHT, Icons.Outlined.LightMode, R.string.settings_theme_light),
    ThemeOption(AppThemeMode.DARK, Icons.Outlined.ModeNight, R.string.settings_theme_dark),
    ThemeOption(AppThemeMode.SYSTEM, Icons.Outlined.Smartphone, R.string.settings_theme_system),
)

private class ColorOption(val color: Color, @StringRes val labelRes: Int)

/**
 * Pilihan warna aksen. [PrimaryCyan] didahulukan karena itu warna bawaan aplikasi —
 * tanpanya, pengguna yang belum pernah memilih apa pun melihat sheet tanpa satu pun
 * opsi tercentang.
 */
private val ColorOptions = listOf(
    ColorOption(PrimaryCyan, R.string.settings_color_primary),
    ColorOption(Color.Blue, R.string.settings_color_blue),
    ColorOption(Color.Magenta, R.string.settings_color_magenta),
    ColorOption(Color.Green, R.string.settings_color_green),
    ColorOption(Color.Yellow, R.string.settings_color_yellow),
    ColorOption(Color.Cyan, R.string.settings_color_cyan),
    ColorOption(Color.LightGray, R.string.settings_color_light_gray),
    ColorOption(Color.Black, R.string.settings_color_black),
    ColorOption(Color.Gray, R.string.settings_color_gray),
)

@Composable
fun ThemeColorSheet(
    onThemeClick: (AppThemeMode) -> Unit,
    onColorClick: (Color) -> Unit,
    themeMode: AppThemeMode,
    initialColorSelected: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        FormField(
            title = stringResource(R.string.settings_theme_choose_title),
            icon = Icons.Outlined.LightMode,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ThemeOptions.forEach { option ->
                    ThemeCardOption(
                        modifier = Modifier.weight(1f),
                        icon = option.icon,
                        title = stringResource(option.labelRes),
                        // Dulu ketiganya hardcoded: Terang selalu `true`, Gelap & Sistem
                        // selalu `false`, dan labelnya sama-sama "Bawaan".
                        isSelected = themeMode == option.mode,
                        onClick = { onThemeClick(option.mode) },
                    )
                }
            }
        }
        FormField(
            title = stringResource(R.string.settings_theme_choose_color),
            icon = Icons.Outlined.Palette,
        ) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState())
                        .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ColorOptions.forEach { option ->
                    ColorOptionItem(
                        color = option.color,
                        name = stringResource(option.labelRes),
                        isSelected = initialColorSelected == option.color,
                        onSelect = onColorClick,
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeCardOption(
    icon: ImageVector,
    title: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    // Ikuti warna aksen yang sedang dipilih, bukan PrimaryCyan tetap — kalau tidak,
    // kartu yang terpilih selalu menyala biru walau warna app sudah diganti.
    val accent = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) accent else BorderGray,
                    shape = RoundedCornerShape(16.dp),
                )
                .clickable {
                    onClick()
                }
                .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) accent else BorderGray,
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = if (isSelected) accent else BorderGray,
        )
    }
}

@Composable
fun ColorOptionItem(
    color: Color,
    name: String,
    isSelected: Boolean = false,
    onSelect: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconCircular(
            icon = if (isSelected) Icons.Outlined.CheckCircle else null,
            backgroundColor = color,
            iconSize = 48.dp,
            iconToCountainerRatio = 2,
            // Teks centang harus kontras dengan warna yang dipilih: putih di atas
            // Hitam/Gelap, hitam di atas Kuning/Abu Muda.
            iconColor = if (color.luminance() > 0.5f) Color.Black else Color.White,
            onClick = {
                onSelect(color)
            },
        )

        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextTertiary,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ThemeColorSheetPreview() {
    NexCashApplicationTheme {
        ThemeColorSheet(
            onThemeClick = {},
            onColorClick = {},
            themeMode = AppThemeMode.SYSTEM,
            initialColorSelected = PrimaryCyan,
        )
    }
}
