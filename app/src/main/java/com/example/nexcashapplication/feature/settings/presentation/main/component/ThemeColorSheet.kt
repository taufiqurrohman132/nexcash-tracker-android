package com.example.nexcashapplication.feature.settings.presentation.main.component

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.R
import com.example.nexcashapplication.core.component.FormField
import com.example.nexcashapplication.core.component.IconCircular
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import com.example.nexcashapplication.ui.theme.TextTertiary

@Composable
fun ThemeColorSheet(
    onColorClick: (Color) -> Unit,
    onThemeClick: () -> Unit,
    initialThemeSelected: String,
    intitalColorSelected: Color,
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
                ThemeCardOption(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.LightMode,
                    title = stringResource(R.string.settings_theme_default),
                    isSelected = true,
                )
                ThemeCardOption(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.ModeNight,
                    title = stringResource(R.string.settings_theme_default),
                    isSelected = false,
                )
                ThemeCardOption(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Smartphone,
                    title = stringResource(R.string.settings_theme_default),
                    isSelected = false,
                )
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
                ColorOptionItem(
                    color = Color.Blue,
                    name = stringResource(R.string.settings_color_blue),
                    isSelected = intitalColorSelected == Color.Blue,
                    oncClick = onColorClick,
                )
                ColorOptionItem(
                    color = Color.Magenta,
                    name = stringResource(R.string.settings_color_magenta),
                    isSelected = intitalColorSelected == Color.Magenta,
                    oncClick = onColorClick,
                )
                ColorOptionItem(
                    color = Color.Green,
                    name = stringResource(R.string.settings_color_green),
                    isSelected = intitalColorSelected == Color.Green,
                    oncClick = onColorClick,
                )
                ColorOptionItem(
                    color = Color.Yellow,
                    name = stringResource(R.string.settings_color_yellow),
                    isSelected = intitalColorSelected == Color.Yellow,
                    oncClick = onColorClick,
                )
                ColorOptionItem(
                    color = Color.Cyan,
                    name = stringResource(R.string.settings_color_cyan),
                    isSelected = intitalColorSelected == Color.Cyan,
                    oncClick = onColorClick,
                )
                ColorOptionItem(
                    color = Color.LightGray,
                    name = stringResource(R.string.settings_color_light_gray),
                    isSelected = intitalColorSelected == Color.LightGray,
                    oncClick = onColorClick,
                )
                ColorOptionItem(
                    color = Color.Black,
                    name = stringResource(R.string.settings_color_black),
                    isSelected = intitalColorSelected == Color.Black,
                    oncClick = onColorClick,
                )
                ColorOptionItem(
                    color = Color.Gray,
                    name = stringResource(R.string.settings_color_gray),
                    isSelected = intitalColorSelected == Color.Gray,
                    oncClick = onColorClick,
                )
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
    Column(
        modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) PrimaryCyan else BorderGray,
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
            tint = if (isSelected) PrimaryCyan else BorderGray,
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = if (isSelected) PrimaryCyan else BorderGray,
        )
    }
}

@Composable
fun ColorOptionItem(
    color: Color,
    name: String,
    isSelected: Boolean = false,
    oncClick: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconCircular(
            icon = if (isSelected) Icons.Outlined.CheckCircle else null,
            backgroundColor = color,
            iconSize = 48.dp,
            iconToCountainerRatio = 2,
            iconColor = Color.White,
            onClick = {
                oncClick(color)
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
            onColorClick = {

            },
            onThemeClick = {

            },
            initialThemeSelected = "Default",
            intitalColorSelected = Color.Blue,
        )
    }
}