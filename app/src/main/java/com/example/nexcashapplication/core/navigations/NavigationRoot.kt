//package com.example.nexcashapplication.core.navigations
//
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.material3.Scaffold
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.navigation.compose.NavHost
//
//private val BottomNavHeight = 80.dp // sesuaikan dengan tinggi asli BottomNavBar kamu
//
//@Composable
//fun NavigationRoot(modifier: Modifier = Modifier) {
//    val navigationState = rememberNavigationState(
//        startRoute = Route.HomeRoute.Main,
//        topLevelRoutes = TOP_LEVEL_DESTINATION.keys,
//    )
//    val navigator = remember {
//        Navigator(navigationState)
//    }
//
//    val currentStack = navigationState.backStack[navigationState.topLevelRoute]
//    val currentRoute = currentStack?.lastOrNull() ?: navigationState.topLevelRoute
//
//    val showBottomBar = currentRoute == navigationState.topLevelRoute
//
//    // LANGSUNG PAKE BOX UTAMA
//    Scaffold(
//        bottomBar = {
//            TabView(tabBarItems, navController)
//        },
//    ) {
//        NavHost(navController = navController, startDestination = homeTab.title) {
//            composable(homeTab.title) {
//                Text(homeTab.title)
//            }
//            composable(alertsTab.title) {
//                Text(alertsTab.title)
//            }
//            composable(settingsTab.title) {
//                Text(settingsTab.title)
//            }
//            composable(moreTab.title) {
//                MoreView()
//            }
//        }
//    }
//    // Overlay BottomNavBar kamu tetap aman di sini
//    AnimatedVisibility(
//        visible = showBottomBar,
//        modifier = Modifier.align(Alignment.BottomCenter),
//        enter = fadeIn(),
//        exit = fadeOut(),
//    ) {
//        BottomNavBar(
//            selectionKey = navigationState.topLevelRoute,
//            onSelectKey = { navigator.navigate(it) },
//        )
//    }
//}