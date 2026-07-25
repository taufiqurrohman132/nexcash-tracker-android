package com.example.nexcashapplication.core.navigations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.nexcashapplication.feature.category.presentation.addnew.category.AddCategoryScreen
import com.example.nexcashapplication.feature.category.presentation.detail.DetailCategoryScreen
import com.example.nexcashapplication.feature.category.presentation.main.CategoryScreen
import com.example.nexcashapplication.feature.dashboard.presentation.ui.DashboardScreen
import com.example.nexcashapplication.feature.transaction.presentation.addnew.AddTransactionScreen
import com.example.nexcashapplication.feature.transaction.presentation.main.TransactionScreen

private val BottomNavHeight = 80.dp // sesuaikan dengan tinggi asli BottomNavBar kamu

@Composable
fun NavigationRoot(modifier: Modifier = Modifier) {
    val navigationState = rememberNavigationState(
        startRoute = Route.HomeRoute.Main,
        topLevelRoutes = TOP_LEVEL_DESTINATION.keys,
    )
    val navigator = remember {
        Navigator(navigationState)
    }

    val currentStack = navigationState.backStack[navigationState.topLevelRoute]
    val currentRoute = currentStack?.lastOrNull() ?: navigationState.topLevelRoute

    val showBottomBar = currentRoute == navigationState.topLevelRoute

    // LANGSUNG PAKE BOX UTAMA
    Box(
        modifier = modifier
                .fillMaxSize(),
    ) {
        NavDisplay(
            modifier = Modifier.fillMaxSize(),
            onBack = navigator::goBack,
            transitionSpec = {
                // screen baru masuk — muncul dari agak kecil, membesar ke normal + fade in
                (scaleIn(
                    initialScale = 0.92f,
                    animationSpec = tween(250),
                ) + fadeIn(tween(250))) togetherWith
                        // screen lama keluar — sedikit membesar (kesan "didorong ke belakang") + fade out
                        (scaleOut(targetScale = 1.05f, animationSpec = tween(200)) + fadeOut(
                            tween(
                                150,
                            ),
                        ))
            },
            // --- animasi balik (pop / back) ---
            popTransitionSpec = {
                // masuk (screen sebelumnya muncul lagi) — scale up dikit dari kecil + fade in
                (scaleIn(initialScale = 0.9f) + fadeIn()) togetherWith
                        // keluar (screen saat ini) — scale down + fade out, kesan "mengecil hilang"
                        (scaleOut(targetScale = 0.85f) + fadeOut())
            },
            // --- animasi kalau predictive back di-swipe tapi dibatalkan ---
            predictivePopTransitionSpec = {
                (scaleIn(initialScale = 0.9f) + fadeIn()) togetherWith
                        (scaleOut(targetScale = 0.8f, animationSpec = tween(300)) + fadeOut(
                            animationSpec = tween(200),
                        ))
            },
            entries = navigationState.toEntries(
                entryProvider {
                    entry<Route.HomeRoute.Main> {
                        Box(
                            Modifier
                                    .fillMaxSize()
                                    .padding(bottom = BottomNavHeight),
                        ) {
                            DashboardScreen()
                        }
                    }
                    entry<Route.TransactionRoute.Main> {
                        Box(
                            Modifier
                                    .fillMaxSize()
                                    .padding(bottom = BottomNavHeight),
                        ) {
                            TransactionScreen(
                                onNavigateToAddTransaction = { navigator.navigate(Route.TransactionRoute.AddNew) },
                                onNavigateToDetail = { id ->
                                    navigator.navigate(
                                        Route.TransactionRoute.Detail(
                                            id,
                                        ),
                                    )
                                },
                            )
                        }
                    }
                    entry<Route.TransactionRoute.AddNew> {
                        AddTransactionScreen()
                    }
                    entry<Route.AssetRoute.Main> {

                    }
                    entry<Route.InsightRoute.Main> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Insight")
                        }
                    }
                    entry<Route.SettingsRoute.Main> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Settings")

                        }
                    }

                    entry<Route.CategorRoute.Main> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CategoryScreen(
                                onNavigateAddCategory = { navigator.navigate(Route.CategorRoute.AddNew) },
                                onNavigateCategoryDetail = { id ->
                                    navigator.navigate(
                                        Route.CategorRoute.Detail(
                                            id,
                                        ),
                                    )
                                },

                                )
                        }
                    }
                    entry<Route.CategorRoute.AddNew> {
                        AddCategoryScreen(
                            onBack = navigator::goBack,
                        )
                    }
                    entry<Route.CategorRoute.Detail> {
                        DetailCategoryScreen()
                    }
                },
            ),
        )

        // Overlay BottomNavBar kamu tetap aman di sini
        AnimatedVisibility(
            visible = showBottomBar,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            BottomNavBar(
                selectionKey = navigationState.topLevelRoute,
                onSelectKey = { navigator.navigate(it) },
            )
        }
    }
}