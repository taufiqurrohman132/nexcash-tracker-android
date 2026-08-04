//package com.example.nexcashapplication.core.navigations
//
//import kotlinx.serialization.Serializable
//
//@Serializable
//sealed interface Screen {
//
//    @Serializable
//    sealed interface HomeScreen : Screen {
//        @Serializable
//        data object Main : HomeScreen
//    }
//
//    @Serializable
//    sealed interface TransactionScreen : Screen {
//        @Serializable
//        data object Main : TransactionScreen
//
//        @Serializable
//        data object AddNew : TransactionScreen
//
//        @Serializable
//        data class Detail(val transactionId: String) : TransactionScreen
//    }
//
//    @Serializable
//    sealed interface AssetScreen : Screen {
//        @Serializable
//        data object Main : AssetScreen
//    }
//
//    @Serializable
//    sealed interface InsightScreen : Screen {
//        @Serializable
//        data object Main : InsightScreen
//    }
//
//    @Serializable
//    sealed interface SettingsScreen : Screen {
//        @Serializable
//        data object Main : SettingsScreen
//    }
//
//    @Serializable
//    sealed interface CategoryScreen : Screen {
//        @Serializable
//        data object Main : CategoryScreen
//
//        @Serializable
//        data class Detail(val categoryId: String) : CategoryScreen
//
//        @Serializable
//        data class AddNew(val categoryId: String, val parentId: String? = null) : CategoryScreen
//    }
//}