package com.example.nexcashapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.nexcashapplication.ui.theme.NexCashApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NexCashApplicationTheme(
                dynamicColor = false, darkTheme = false,
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NexcashApp()
                }
            }
        }
    }
}

