package com.example.nexcashapplication.core.navigations

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.nexcashapplication.feature.aset.presentation.addedit.form.AddEditInfoAccountScreen
import com.example.nexcashapplication.feature.aset.presentation.addedit.typeoption.AddEditTypeAccountScreen
import com.example.nexcashapplication.feature.aset.presentation.detail.DetailAccountScreen
import com.example.nexcashapplication.feature.aset.presentation.main.AccountAssetScreen
import com.example.nexcashapplication.feature.category.presentation.addnew.AddCategoryScreen
import com.example.nexcashapplication.feature.category.presentation.delete.DeleteCategoryScreen
import com.example.nexcashapplication.feature.category.presentation.detail.DetailCategoryScreen
import com.example.nexcashapplication.feature.category.presentation.main.CategoryScreen
import com.example.nexcashapplication.feature.dashboard.presentation.ui.DashboardScreen
import com.example.nexcashapplication.feature.transaction.presentation.addnew.AddEditTransactionScreen
import com.example.nexcashapplication.feature.transaction.presentation.main.TransactionScreen
import com.example.nexcashapplication.feature.transaction.presentation.ui.DetailTransactionScreen

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

        composable<Route.TransactionRoute.Detail> {
            DetailTransactionScreen()
        }

        composable<Route.TransactionRoute.AddNew> {
            AddEditTransactionScreen(
                navController = navController,
            )
        }

        composable<Route.TransactionRoute.Main> {
            TransactionScreen(
                navController = navController,
            )
        }

        composable<Route.CategoryRoute.Detail> {
            DetailCategoryScreen(
                navController = navController,
            )
        }

        composable<Route.CategoryRoute.AddNew> {
            AddCategoryScreen(
                navController = navController,
            )
        }
        composable<Route.CategoryRoute.Delete> {
            DeleteCategoryScreen(
                navController = navController,
            )
        }



        composable<Route.AccountRoute.Main> {
            AccountAssetScreen(navController = navController)
        }
        composable<Route.AccountRoute.Detail> {
            DetailAccountScreen(navController = navController)
        }
        composable<Route.AccountRoute.AddEditInfo> {
            AddEditInfoAccountScreen(navController = navController)
        }
        composable<Route.AccountRoute.AddEditTypeAccount> {
            AddEditTypeAccountScreen(navController = navController)
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