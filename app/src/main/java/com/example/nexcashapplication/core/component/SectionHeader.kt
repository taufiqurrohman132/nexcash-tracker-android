package com.example.nexcashapplication.core.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexcashapplication.ui.theme.PrimaryCyan


@Composable
fun FormField(
    title: String,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    fontWeight: FontWeight= FontWeight.SemiBold,
    icon: ImageVector? = null,
    actionText: String? = null,
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    Column {

        Row(
            modifier = modifier.padding(top = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon?.let {
                    Icon(it, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                }

                Text(
                    text = title,
                    style = textStyle,
                    fontWeight = fontWeight
                )
            }

            actionText?.let {
                TextButton(onClick = { onActionClick() }) {
                    Text(
                        text = actionText,
                        color = PrimaryCyan
                    )
                }

            }
        }
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun FormFieldPreview() {
    FormField(
        title = "Pemasukan",
        actionText = "Lihat semua"
    )

}