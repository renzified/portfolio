package com.rsalanga.roadmap.pages.home.navigation

import androidx.annotation.DrawableRes
import androidx.navigation3.runtime.NavKey
import com.rsalanga.roadmap.R
import kotlinx.serialization.Serializable

@Serializable
internal data object CalendarRoute : NavKey

@Serializable
internal data object PlansRoute : NavKey

@Serializable
internal data object CoachRoute : NavKey

@Serializable
internal data object SettingsRoute : NavKey

@Serializable
internal data object ConversationRoute : NavKey

internal data class HomeTab(val label: String, @param:DrawableRes val icon: Int)

/** Bottom nav tabs, in display order. The first entry is the start route. */
internal val HomeTabs: Map<NavKey, HomeTab> = linkedMapOf(
    CalendarRoute to HomeTab(label = "Calendar", icon = R.drawable.ic_calendar),
    PlansRoute to HomeTab(label = "Plans", icon = R.drawable.ic_plans),
    CoachRoute to HomeTab(label = "Coach", icon = R.drawable.ic_coach),
    SettingsRoute to HomeTab(label = "Settings", icon = R.drawable.ic_settings_tune)
)
