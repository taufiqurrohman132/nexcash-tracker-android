package com.example.nexcashapplication.core.component

import android.text.style.LineBackgroundSpan
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.size.Size
import com.example.nexcashapplication.R
import com.example.nexcashapplication.ui.theme.PrimaryCyan

@Composable
fun IconRounded(
    modifier: Modifier = Modifier,
    iconColor: Color = Color.Black,
    backgroundColor: Color = Color.White,
    size: Dp = 40.dp,
    shape: Shape = RoundedCornerShape(8.dp),
    imageVector: ImageVector,
) {

    Box(
        modifier = modifier
                .background(
                    color = backgroundColor,
                    shape = shape
                )
                .size(size),
        contentAlignment = Alignment.Center
    ){
        Icon(
            imageVector = imageVector,
            tint = iconColor,
            contentDescription = null,
            modifier = Modifier.size(size / 2)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun IconCircularPreview() {
    
}
