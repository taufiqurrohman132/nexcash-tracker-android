package com.example.nexcashapplication.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.ui.theme.BorderGray
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import com.example.nexcashapplication.ui.theme.TextTertiary
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale


/**
 * Navigasi bulan di TopAppBar: pill putih berisi ‹ label-bulan ›.
 * Label tampil normal (bukan bold) dan menjadi trigger [onOpenPicker];
 * panah kanan dinonaktifkan (redup, tidak bisa diklik) saat berada di
 * bulan berjalan — masa depan tidak punya data.
 */
@Composable
fun MonthNavigator(
    month: YearMonth,
    onMonthSelected: (YearMonth) -> Unit,
    onOpenPicker: () -> Unit,
    modifier: Modifier = Modifier,
) {
//    val canGoNext = month < YearMonth.now()
//    val chevronTint = Color(0xFF9E9E9E)

    Row(
        modifier = modifier
                .clip(RoundedCornerShape(50))
                .border(2.dp, BorderGray, RoundedCornerShape(50))
                .background(Color.White)
                .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        IconCircular(
            iconColor = TextTertiary,
            icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            iconToCountainerRatio = 2,
            onClick = { onMonthSelected(month.minusMonths(1)) },
        )

        Row(
            modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50))
                    .clickable { onOpenPicker() }
                    .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.DateRange,
                contentDescription = null,
                tint = PrimaryCyan,
                modifier = Modifier
                        .size(20.dp)
                        .padding(end = 4.dp),
            )
            Text(
                text = "${
                    month.month.getDisplayName(
                        TextStyle.SHORT,
                        Locale.getDefault(),
                    )
                } ${month.year}",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            )

        }

        IconCircular(
            iconColor = TextTertiary,
            icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            iconToCountainerRatio = 2,
            onClick = { onMonthSelected(month.plusMonths(1)) },
        )

    }
}
