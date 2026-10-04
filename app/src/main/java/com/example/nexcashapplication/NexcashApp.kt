package com.example.nexcashapplication

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.nexcashapplication.core.navigations.BottomNavBar
import com.example.nexcashapplication.core.navigations.NavigationStack
import com.example.nexcashapplication.core.navigations.Route

/**
 * @param pendingDestination layar tujuan dari notifikasi yang di-tap, atau null.
 *   Di-navigasi lewat efek (bukan lewat `startDestination`) supaya notifikasi selalu membuka
 *   layar tujuan **di atas** Dashboard — kalau dijadikan start destination, tombol Kembali akan
 *   keluar aplikasi, bukan kembali ke Dashboard.
 * @param onDestinationConsumed dipanggil setelah navigasi, supaya layar yang sama tidak
 *   dinavigasi ulang tiap recomposition (dan tujuan lama tidak terpakai lagi saat kembali).
 */
@Composable
fun NexcashApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    pendingDestination: Route? = null,
    onDestinationConsumed: () -> Unit = {},
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

    // Ditaruh setelah Scaffold/NavHost supaya graph sudah terpasang saat effect ini jalan.
    LaunchedEffect(pendingDestination) {
        val destination = pendingDestination ?: return@LaunchedEffect
        navController.navigate(destination) { launchSingleTop = true }
        onDestinationConsumed()
    }
}
