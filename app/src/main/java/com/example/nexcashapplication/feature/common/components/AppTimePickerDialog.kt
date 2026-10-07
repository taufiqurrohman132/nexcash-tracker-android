package com.example.nexcashapplication.feature.common.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.nexcashapplication.R
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import com.example.nexcashapplication.ui.theme.SecondaryDeepPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true,
    )

    TimePickerDialog(
        title = { Text(stringResource(R.string.core_time_picker_title)) },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(timePickerState.hour, timePickerState.minute)
                    onDismiss()
                },
            ) {
                Text(stringResource(R.string.common_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_cancel)) }
        },

        ) {
        TimePicker(
            state = timePickerState,
            colors = TimePickerDefaults.colors(
                // Container keseluruhan dialog
                containerColor = Color.White,

                // Clock dial (lingkaran jam)
                clockDialColor = Color(0xFFF5F5F5),
                clockDialSelectedContentColor = Color.White,
                clockDialUnselectedContentColor = Color(0xFF1A1A1A),
                selectorColor = PrimaryCyan, // jarum jam + titik selected

                // Time selector (kotak jam & menit di atas)
                timeSelectorSelectedContainerColor = PrimaryCyan.copy(alpha = 0.12f),
                timeSelectorUnselectedContainerColor = Color(0xFFF5F5F5),
                timeSelectorSelectedContentColor = SecondaryDeepPurple,
                timeSelectorUnselectedContentColor = Color(0xFF1A1A1A),

                // Period selector (AM/PM toggle)
                periodSelectorBorderColor = SecondaryDeepPurple.copy(alpha = 0.3f),
                periodSelectorSelectedContainerColor = PrimaryCyan,
                periodSelectorUnselectedContainerColor = Color.White,
                periodSelectorSelectedContentColor = Color.White,
                periodSelectorUnselectedContentColor = Color(0xFF1A1A1A),
            ),
        )
    }


}