//package com.example.nexcashapplication.core.navigations
//
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.saveable.rememberSerializable
//import androidx.compose.runtime.setValue
//import androidx.compose.runtime.snapshots.SnapshotStateList
//import androidx.compose.runtime.toMutableStateList
//import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
//import androidx.navigation3.runtime.NavBackStack
//import androidx.navigation3.runtime.NavEntry
//import androidx.navigation3.runtime.NavKey
//import androidx.navigation3.runtime.rememberDecoratedNavEntries
//import androidx.navigation3.runtime.rememberNavBackStack
//import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
//import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
//import androidx.savedstate.serialization.SavedStateConfiguration
//import kotlinx.serialization.PolymorphicSerializer
//import kotlinx.serialization.modules.SerializersModule
//import kotlinx.serialization.modules.polymorphic
//
//class NavigationState(
//    val startRoute: NavKey,
//    topLevelRoute: MutableState<NavKey>,
//    val backStack: Map<NavKey, NavBackStack<NavKey>>,
//) {
//    var topLevelRoute by topLevelRoute
//
//    val stacksInUse: List<NavKey>
//        get() = if (topLevelRoute == startRoute) {
//            listOf(startRoute)
//        } else {
//            listOf(startRoute, topLevelRoute)
//        }
//}
//
//val serializersConfig = SavedStateConfiguration {
//    serializersModule = SerializersModule {
//        polymorphic(NavKey::class) {
//            subclass(Route.HomeRoute.Main::class, Route.HomeRoute.Main.serializer())
//            subclass(Route.TransactionRoute.Main::class, Route.TransactionRoute.Main.serializer())
//            subclass(Route.AssetRoute.Main::class, Route.AssetRoute.Main.serializer())
//            subclass(Route.CategoryRoute.Main::class, Route.CategoryRoute.Main.serializer())
//            subclass(Route.SettingsRoute.Main::class, Route.SettingsRoute.Main.serializer())
//        }
//    }
//}
//
//@Composable
//fun rememberNavigationState(
//    startRoute: NavKey,
//    topLevelRoutes: Set<NavKey>,
//): NavigationState {
//    val topLevelRoute = rememberSerializable(
//        startRoute,
//        topLevelRoutes,
//        configuration = serializersConfig,
//        serializer = MutableStateSerializer(PolymorphicSerializer(NavKey::class)),
//    ) {
//        mutableStateOf(startRoute)
//    }
//
//    val backStack = topLevelRoutes.associateWith { key ->
//        rememberNavBackStack(
//            configuration = serializersConfig,
//            key,
//        )
//    }
//
//    return remember(startRoute, topLevelRoutes) {
//        NavigationState(
//            startRoute = startRoute,
//            topLevelRoute = topLevelRoute,
//            backStack = backStack,
//        )
//    }
//
//}
//
//@Composable
//fun NavigationState.toEntries(
//    entryProvider: (NavKey) -> NavEntry<NavKey>,
//): SnapshotStateList<NavEntry<NavKey>> {
//    val decoratedEntries = backStack.mapValues { (_, stack) ->
//        val decorators = listOf(
//            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
//            rememberViewModelStoreNavEntryDecorator(),
//        )
//        rememberDecoratedNavEntries(
//            backStack = stack,
//            entryDecorators = decorators,
//            entryProvider = entryProvider,
//        )
//    }
//
//    return stacksInUse
//            .flatMap { decoratedEntries[it] ?: emptyList() }
//            .toMutableStateList()
//
//}