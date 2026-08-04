package com.example.nexcashapplication.core.navigations

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.nexcashapplication.feature.category.presentation.addnew.category.AddCategoryScreen
import com.example.nexcashapplication.feature.category.presentation.detail.DetailCategoryScreen
import com.example.nexcashapplication.feature.category.presentation.main.CategoryScreen
import com.example.nexcashapplication.feature.dashboard.presentation.ui.DashboardScreen

@Composable
fun NavigationStack(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {

    NavHost(
        navController = navController,
        startDestination = Route.HomeRoute.Main,
        modifier = modifier,
    ) {
        composable<Route.HomeRoute.Main> {
            DashboardScreen()
        }

        composable<Route.TransactionRoute.Detail> { entry ->
            val args = entry.toRoute<Route.TransactionRoute.Detail>()
            // TransactionDetailScreen(transactionId = args.transactionId)
        }

        composable<Route.CategoryRoute.Detail> { entry ->
            val args = entry.toRoute<Route.CategoryRoute.Detail>()
            DetailCategoryScreen(
                categoryId = args.categoryId,
                navController = navController,
            )
        }

        composable<Route.CategoryRoute.AddNew> { entry ->
            val args = entry.toRoute<Route.CategoryRoute.AddNew>()
            AddCategoryScreen(
                categoryId = args.categoryId,
                parentId = args.parentId,
                navController = navController,
            )
        }

        composable<Route.TransactionRoute.AddNew> {
//             TransactionAddNewScreen()
        }

        composable<Route.AssetRoute.Main> {
            // AssetScreen()
        }

        composable<Route.InsightRoute.Main> {
            // InsightScreen()
        }

        composable<Route.SettingsRoute.Main> {
            // SettingsScreen()
        }

        composable<Route.CategoryRoute.Main> {
            CategoryScreen(
                navController = navController,
            )
        }
    }
}