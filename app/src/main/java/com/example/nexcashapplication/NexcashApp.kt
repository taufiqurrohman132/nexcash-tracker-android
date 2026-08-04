package com.example.nexcashapplication

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.nexcashapplication.core.navigations.BottomNavBar
import com.example.nexcashapplication.core.navigations.NavigationStack

@Composable
fun NexcashApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    // Outer Scaffold (level App, ada BottomNavBar)
    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0), // <- matikan insets di sini
        modifier = modifier,
    ) { innerPadding ->
        NavigationStack(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}