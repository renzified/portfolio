package com.rsalanga.roadmap.pages.calendar

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.rsalanga.roadmap.pages.calendar.component.CalendarScreenTopAppBar
import com.rsalanga.roadmap.ui.RoadmapTheme

@Composable
internal fun CalendarScreen() {
    Scaffold(
        topBar = {
            CalendarScreenTopAppBar(
                title = "October",
                yearAndWeek = "2026 · Week 44"
            )
        }
    ) { _ ->

    }
}

@Preview
@Composable
private fun CalendarScreenPreview() {
    RoadmapTheme {
        CalendarScreen()
    }
}