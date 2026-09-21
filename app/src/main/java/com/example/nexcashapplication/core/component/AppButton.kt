package com.example.nexcashapplication.core.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import com.example.nexcashapplication.ui.theme.PrimaryCyan
import com.example.nexcashapplication.ui.theme.PrimaryCyanDisable

@Composable
fun AppButton(
    isEnabled: Boolean = true,
    title: String,
    onClick: () -> Unit,
    backgroundColor: Color = PrimaryCyan,
    disableBackgroundColor: Color = PrimaryCyanDisable,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier,
) {
    Button(
        enabled = isEnabled,
        onClick = { onClick() },
        modifier = modifier
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = backgroundColor,
                    spotColor = backgroundColor,
                )
                .fillMaxWidth()
                .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            disabledContainerColor = disableBackgroundColor,
        ),
    ) {
        Text(
            title,
            fontWeight = FontWeight.Bold,
            color = textColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppButtonPreveiw() {
    NexCashApplicationTheme {
        AppButton(
            title = "Button",
            onClick = {},
        )
    }
}