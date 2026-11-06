package com.example.nexcashapplication.core.component


// Add these imports if missing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexcashapplication.R
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale

@Composable
fun MonthPicker(
    visible: Boolean,
    currentMonth: Int,
    currentYear: Int,
    confirmButtonCLicked: (Int, Int) -> Unit,
    cancelClicked: () -> Unit,
) {
    // Nama bulan singkat mengikuti bahasa aplikasi (dulu hardcoded "JAN".."DEC").
    // Urutan tetap Jan..Des sehingga months[currentMonth] dan indexOf(month) tetap valid.
    val months = (1..12).map { month ->
        java.time.Month.of(month)
                .getDisplayName(TextStyle.SHORT_STANDALONE, LocalLocale.current.platformLocale)
                .uppercase(LocalLocale.current.platformLocale)
    }

    var month by remember { mutableStateOf(months[currentMonth]) }
    var year by remember { mutableIntStateOf(currentYear) } // Use mutableIntStateOf for efficiency
    val interactionSource = remember { MutableInteractionSource() }

    if (visible) {
        AlertDialog(
            onDismissRequest = cancelClicked,
            confirmButton = {
                TextButton(onClick = { confirmButtonCLicked(months.indexOf(month), year) }) {
                    Text(stringResource(R.string.common_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = cancelClicked) {
                    Text(stringResource(R.string.common_cancel))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(10.dp),
            title = {
                Text(
                    text = stringResource(R.string.core_month_picker_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            modifier = Modifier
                                    .size(35.dp)
                                    .rotate(90f)
                                    .clickable(
                                        indication = null,
                                        interactionSource = interactionSource,
                                        onClick = { year-- },
                                    ),
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = null,
                        )

                        Text(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            text = year.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )

                        Icon(
                            modifier = Modifier
                                    .size(35.dp)
                                    .rotate(-90f)
                                    .clickable(
                                        indication = null,
                                        interactionSource = interactionSource,
                                        onClick = { year++ },
                                    ),
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = null,
                        )
                    }

                    Card(
                        modifier = Modifier
                                .padding(top = 30.dp)
                                .fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    ) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            months.forEach { item ->
                                Box(
                                    modifier = Modifier
                                            .size(60.dp)
                                            .clickable(
                                                indication = null,
                                                interactionSource = interactionSource,
                                                onClick = { month = item },
                                            ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    val animatedSize by animateDpAsState(
                                        targetValue = if (month == item) 60.dp else 0.dp,
                                        animationSpec = tween(
                                            durationMillis = 500,
                                            easing = LinearOutSlowInEasing,
                                        ),
                                    )

                                    Box(
                                        modifier = Modifier
                                                .size(animatedSize)
                                                .background(
                                                    color = if (month == item) Color.Blue else Color.Transparent,
                                                    shape = CircleShape,
                                                ),
                                    )

                                    Text(
                                        text = item,
                                        color = if (month == item) Color.White else Color.Black,
                                        fontWeight = FontWeight.Medium,
                                    )
                                }
                            }
                        }
                    }
                }
            },
        )
    }
}
