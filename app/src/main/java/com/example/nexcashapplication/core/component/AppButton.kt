package com.example.nexcashapplication.core.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme

@Composable
fun AppButton(
    isEnabled: Boolean = true,
    title: String,
    onClick: () -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    disableBackgroundColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    disabledTextColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
    modifier: Modifier = Modifier,
) {
    Button(
        enabled = isEnabled,
        onClick = onClick,
        modifier = modifier
            .shadow(
                elevation = if (isEnabled) 8.dp else 0.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = backgroundColor,
                spotColor = backgroundColor,
            )
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor,
            disabledContainerColor = disableBackgroundColor,
            disabledContentColor = disabledTextColor,
        ),
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            color = if (isEnabled) textColor else disabledTextColor,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppButtonPreview() {
    NexCashApplicationTheme {
        AppButton(
            title = "Button",
            onClick = {},
        )
    }
}
