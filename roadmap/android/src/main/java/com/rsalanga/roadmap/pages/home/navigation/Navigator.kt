package com.rsalanga.roadmap.pages.home.navigation

import androidx.navigation3.runtime.NavKey

/** Handles navigation events by updating [NavigationState]. */
internal class Navigator(private val state: NavigationState) {

    fun navigate(route: NavKey) {
        if (route in state.backStacks.keys) {
            state.topLevelRoute = route
        } else {
            state.backStacks.getValue(state.topLevelRoute).add(route)
        }
    }

    fun goBack() {
        val currentStack = state.backStacks.getValue(state.topLevelRoute)

        // At the root of a non-start tab, back returns to the start tab.
        if (currentStack.last() == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }
}
