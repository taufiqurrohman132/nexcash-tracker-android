package com.example.nexcashapplication.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexcashapplication.ui.theme.PrimaryCyan

@Composable
fun IconCircular(
    modifier: Modifier = Modifier,
    iconColor: Color = Color.Black,
    backgroundColor: Color = Color.White,
    iconSize: Dp = 40.dp,
//    iconTextSize: TextUnit = 20.sp,
    iconToCountainerRatio: Int = 3,
    icon: ImageVector? = null,
    iconText: String? = null,
    onClick: (() -> Unit)? = null,
) {

    Box(
        modifier = modifier
                .clip(CircleShape)
                .background(
                    color = backgroundColor,
                )
                .size(iconSize)
                .clickable(enabled = onClick != null) {
                    onClick?.invoke()
                },
        contentAlignment = Alignment.Center,
    ) {
        iconText?.let {
            Text(
                textAlign = TextAlign.Center,
                text = iconText,
                color = iconColor,
                fontWeight = FontWeight.Bold,
                fontSize = (iconSize.value.sp / iconToCountainerRatio),
//                style = TextStyle(
//                    lineHeight = (iconSize.value.sp / iconToCountainerRatio), // samain sama fontSize
//                )
            )
        }
        icon?.let {
            Icon(
                imageVector = icon,
                tint = iconColor,
                contentDescription = null,
                modifier = Modifier.size(iconSize / iconToCountainerRatio),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun IconCircularIconPreview() {
    IconCircular(
        icon = Icons.Default.Settings,
        backgroundColor = PrimaryCyan,
    )
}

@Preview(showBackground = true)
@Composable
private fun IconCircularTextPreview() {
    IconCircular(
        iconText = "TR",
        backgroundColor = PrimaryCyan,
    )
}
