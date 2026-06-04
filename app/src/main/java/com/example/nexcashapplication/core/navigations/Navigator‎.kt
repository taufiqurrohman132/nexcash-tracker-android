package com.example.nexcashapplication.core.navigations

import androidx.navigation3.runtime.NavKey

class Navigator(val state: NavigationState) {
    fun navigate( route: NavKey) {
        if (route in state.backStack.keys){
            state.topLevelRoute = route
        }else{
            state.backStack[state.topLevelRoute]?.add(route)
        }
    }
    fun goBack() {
        val currentStack = state.backStack[state.topLevelRoute]
            ?: error("Back stack fot ${state.topLevelRoute} daesnot exist")
        val currentRoute = currentStack.last()

        if (currentRoute == state.topLevelRoute){
            state.topLevelRoute = state.startRoute
        }else{
            currentStack.removeLastOrNull()
        }
    }
}