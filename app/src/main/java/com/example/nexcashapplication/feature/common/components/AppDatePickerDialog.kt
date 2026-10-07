package com.example.nexcashapplication.feature.common.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.nexcashapplication.R
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import com.example.nexcashapplication.ui.theme.SecondaryDeepPurple

@Composable
fun AppDatePickerDialog(
    initialDateMillis: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                    onDismiss()
                },
            ) {
                Text(stringResource(R.string.common_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.common_cancel))
            }
        },

        ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = Color.White,

                // Header (bulan/tahun besar di atas)
                headlineContentColor = SecondaryDeepPurple,
                weekdayContentColor = SecondaryDeepPurple.copy(alpha = 0.6f),

                // Selected day
                selectedDayContainerColor = PrimaryCyan,
                selectedDayContentColor = Color.White,
                selectedYearContainerColor = PrimaryCyan,
                selectedYearContentColor = Color.White,

                // Today (hari ini, belum diselect)
                todayContentColor = PrimaryCyan,
                todayDateBorderColor = PrimaryCyan,

                // Day biasa
                dayContentColor = Color(0xFF1A1A1A),
                disabledDayContentColor = Color(0xFF1A1A1A).copy(alpha = 0.3f),

                // Year biasa (di year picker mode)
                yearContentColor = Color(0xFF1A1A1A),
                currentYearContentColor = PrimaryCyan,

                // Navigation icons (arrow prev/next month, dropdown)
                navigationContentColor = SecondaryDeepPurple,

                // Divider
                dividerColor = Color(0xFFE6E6E6),
            ),
        )
    }
}