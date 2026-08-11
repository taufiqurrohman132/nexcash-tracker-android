package com.example.nexcashapplication.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.nexcashapplication.R

@Composable
fun IconEye(
    onEyeClick: () -> Unit
) {
    Box(
        modifier = Modifier
                .size(40.dp)
                .background(
                    color = Color.White.copy(0.2f),
                    shape = CircleShape
                )
                .clip(CircleShape)
                .clickable { onEyeClick() },
        contentAlignment = Alignment.Center,

        ) {
        Icon(
            painter = painterResource(R.drawable.eye_show),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}