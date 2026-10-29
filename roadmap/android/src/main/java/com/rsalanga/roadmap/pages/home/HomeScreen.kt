package com.rsalanga.roadmap.pages.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.rsalanga.roadmap.pages.calendar.CalendarScreen
import com.rsalanga.roadmap.pages.coach.CoachScreen
import com.rsalanga.roadmap.pages.coach.conversation.ConversationScreen
import com.rsalanga.roadmap.pages.home.navigation.CalendarRoute
import com.rsalanga.roadmap.pages.home.navigation.CoachRoute
import com.rsalanga.roadmap.pages.home.navigation.ConversationRoute
import com.rsalanga.roadmap.pages.home.navigation.HomeTabs
import com.rsalanga.roadmap.pages.home.navigation.Navigator
import com.rsalanga.roadmap.pages.home.navigation.PlansRoute
import com.rsalanga.roadmap.pages.home.navigation.SettingsRoute
import com.rsalanga.roadmap.pages.home.navigation.rememberNavigationState
import com.rsalanga.roadmap.pages.plans.PlansScreen
import com.rsalanga.roadmap.pages.settings.SettingScreen
import com.rsalanga.roadmap.ui.RoadmapTheme

@Composable
internal fun HomeScreen() {
    val navigationState = rememberNavigationState(
        startRoute = CalendarRoute,
        topLevelRoutes = HomeTabs.keys,
    )
    val navigator = remember(navigationState) { Navigator(navigationState) }

    val entryProvider = entryProvider {
        entry<CalendarRoute> {
            CalendarScreen(onClickLearnASkill = dropUnlessResumed { navigator.navigate(ConversationRoute) })
        }
        entry<PlansRoute> { PlansScreen() }
        entry<CoachRoute> { CoachScreen() }
        entry<SettingsRoute> { SettingScreen() }
        entry<ConversationRoute> { ConversationScreen(onBack = navigator::goBack) }
    }

    Scaffold(
        // Tab screens handle their own top insets; only the bottom bar is padded out here.
        contentWindowInsets = WindowInsets(),
        bottomBar = {
            NavigationBar {
                HomeTabs.forEach { (route, tab) ->
                    NavigationBarItem(
                        selected = route == navigationState.topLevelRoute,
                        onClick = { navigator.navigate(route) },
                        icon = { Icon(painter = painterResource(tab.icon), contentDescription = null) },
                        label = { Text(tab.label) },
                    )
                }
            }
        }
    ) { innerPadding ->
        NavDisplay(
            entries = navigationState.toDecoratedEntries(entryProvider),
            onBack = navigator::goBack,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        )
    }
}

@Preview
@Composable
private fun PreviewHomeScreen() {
    RoadmapTheme {
        HomeScreen()
    }
}
